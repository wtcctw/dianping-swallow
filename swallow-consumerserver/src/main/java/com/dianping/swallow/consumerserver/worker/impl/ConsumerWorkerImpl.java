package com.dianping.swallow.consumerserver.worker.impl;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.heartbeat.DefaultHeartBeatReceiver;
import com.dianping.swallow.common.internal.heartbeat.HeartBeatReceiver;
import com.dianping.swallow.common.internal.heartbeat.NoHeartBeatListener;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservableLifecycle;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.common.server.monitor.collector.ConsumerCollector;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;
import com.dianping.swallow.consumerserver.util.ConsumerUtil;
import com.dianping.swallow.consumerserver.worker.ConsumerWorker;

/***
 * 一个ConsumerWorkerImpl负责处理一个(topic,consumerId)的消费者集群，使用单线程获取消息，并顺序推送给Consumer
 * 
 * @author kezhu.wu
 */
public final class ConsumerWorkerImpl extends AbstractObservableLifecycle implements ConsumerWorker, NoHeartBeatListener {

	/** 允许"最小的空洞waitAckMessage"存活的时间的阈值,单位秒，默认5分钟 */
	private final long WAIT_ACK_EXPIRED = ConfigManager.getInstance().getWaitAckExpiredSecond() * 1000;

	private ConsumerInfo consumerInfo;
	private MessageFilter messageFilter;

	private SwallowBuffer swallowBuffer;
	private MessageDAO messageDao;
	private AckDAO ackDao;

	private CloseableBlockingQueue<SwallowMessage> messageQueue;
	private ExecutorService ackExecutor;
	private ExecutorService sendMessageExecutor;
	private ConsumerAuthController consumerAuthController;

	private volatile boolean started = false;

	/** 可用来发送消息的channel */
	private Queue<Channel> freeChannels = new ConcurrentLinkedQueue<Channel>();
	/** 存放已连接的channel，key是channel，value是ip */
	private ConcurrentHashMap<Channel, String> connectedChannels = new ConcurrentHashMap<Channel, String>();

	/** 记录当前最大的已经返回ack的消息 */
	private volatile ConsumerMessage maxAckedMessage;
	private volatile ConsumerMessage maxAckedBackupMessage;

	private volatile long lastRecordedAckId = 0L;
	private volatile long lastRecordedBackupAckId = 0L;

	private ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages = new ConcurrentSkipListMap<Long, ConsumerMessage>();
	private ConcurrentSkipListMap<Long, ConsumerMessage> waitAckBackupMessages = new ConcurrentSkipListMap<Long, ConsumerMessage>();

	private ConsumerThreadPoolManager consumerThreadPoolManager;

	private HeartBeatReceiver heartBeatReceiver;

	private ConsumerCollector consumerCollector;
	
	private long startMessageId;

	private AtomicInteger	  messageToSend = new AtomicInteger();
	
	protected final Logger ackLogger = LoggerFactory.getLogger("ackLogger");

	@SuppressWarnings("deprecation")
	public ConsumerWorkerImpl(ConsumerInfo consumerInfo, ConsumerWorkerManager workerManager,
			ConsumerAuthController consumerAuthController,
			ConsumerThreadPoolManager consumerThreadPoolManager, long startMessageId,
			ConsumerCollector consumerCollector) {

		this.consumerInfo = consumerInfo;
		this.ackDao = workerManager.getAckDAO();
		this.messageDao = workerManager.getMessageDAO();
		this.swallowBuffer = workerManager.getSwallowBuffer();
		this.consumerThreadPoolManager = consumerThreadPoolManager;
		this.consumerAuthController = consumerAuthController;
		this.consumerCollector = consumerCollector;
		this.startMessageId = startMessageId;

		// consumerInfo的type不允许AT_MOST模式，遇到则修改成AT_LEAST模式（因为AT_MOST会导致ack插入比较频繁，所以不用它）
		if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_MOST_ONCE) {
			this.consumerInfo.setConsumerType(ConsumerType.DURABLE_AT_LEAST_ONCE);
			logger.warn("ConsumerClient[consumerInfo=" + consumerInfo
					+ "] used ConsumerType.DURABLE_AT_MOST_ONCE. Now change it to ConsumerType.DURABLE_AT_LEAST_ONCE.");
		}

		this.ackExecutor = this.consumerThreadPoolManager.getServiceHandlerThreadPool();
		this.sendMessageExecutor = this.consumerThreadPoolManager.getSendMessageThreadPool();

		heartBeatReceiver = new DefaultHeartBeatReceiver(consumerThreadPoolManager.getScheduledThreadPool(), this);
		
	}

	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();

		long messageIdOfTailMessage = (startMessageId != -1 ? startMessageId : getMaxMessageId(false));
		long messageIdOfTailBackupMessage = -1;
		if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
			messageIdOfTailBackupMessage = (startMessageId != -1 ? startMessageId : getMaxMessageId(true));
		}

		if (logger.isInfoEnabled()) {
			logger.info("[<cinit>][startId]" + messageIdOfTailMessage + "," + messageIdOfTailBackupMessage);
		}
		messageQueue = swallowBuffer.createMessageQueue(this.consumerInfo, messageIdOfTailMessage,
				messageIdOfTailBackupMessage, this.messageFilter);

		addObserver(messageQueue);
	}
	
	@Override
	public void handleAck(final Channel channel, final long ackId, final ACKHandlerType type) {

		ackExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String consumerIp = IPUtil.getIpFromChannel(channel);

					if (ackLogger.isInfoEnabled()) {
						ackLogger.info(consumerInfo.getDest().getName() + "," + consumerInfo.getConsumerId() + ","
								+ ackId + "," + IPUtil.simpleLogIp(connectedChannels.get(channel)));
					}

					ConsumerMessage message = removeWaitAckMessages(channel, ackId);
					if (message != null) {
						consumerCollector.ackMessage(consumerInfo, consumerIp, message.message);
					}

					if (ACKHandlerType.CLOSE_CHANNEL.equals(type)) {
						if (logger.isInfoEnabled()) {
							logger.info("receive ack(type=" + type + ") from " + consumerIp);
						}
						channel.close();
					} else if (ACKHandlerType.SEND_MESSAGE.equals(type)) {
						freeChannels.add(channel);
					}
				} catch (Exception e) {
					logger.error("handleAck wrong!", e);
				}
			}
		});

	}

	@Override
	public void handleHeartBeat(Channel channel) {
		heartBeatReceiver.beat(channel);
	}

	@Override
	public void onNoHeartBeat(Channel channel) {
		if (logger.isInfoEnabled()) {
			logger.info("[onNoHeartBeat][close channel]" + channel);
		}
		channel.close();
	}

	private ConsumerMessage removeWaitAckMessages(Channel channel, long ackId) {
		ConsumerMessage waitAckMessage = waitAckMessages.remove(ackId);

		if (waitAckMessage != null) {
			if (maxAckedMessage == null || waitAckMessage.getAckId() > maxAckedMessage.getAckId()) {
				maxAckedMessage = waitAckMessage;
			}
			catTraceForAck(ackId, channel);
		} else {
			waitAckMessage = waitAckBackupMessages.remove(ackId);
			if (waitAckMessage != null) {
				if (maxAckedBackupMessage == null || waitAckMessage.getAckId() > maxAckedBackupMessage.getAckId()) {
					maxAckedBackupMessage = waitAckMessage;
				}
				catTraceForBackupAck(ackId, channel);
			}
		}

		return waitAckMessage;
	}

	@Override
	public void recordAck() {

		recordAck0(waitAckMessages, maxAckedMessage, false);
		recordAck0(waitAckBackupMessages, maxAckedBackupMessage, true);

	}

	private void recordAck0(ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages0, ConsumerMessage maxAckedMessage0, boolean isBackup) {
		
		Entry<Long, ConsumerMessage> entry;
		while ((entry = waitAckMessages0.firstEntry()) != null) {// 使用while，尽最大可能消除空洞ack
																	// id
			boolean overdue = false;// 是否超过阈值

			ConsumerMessage minWaitAckMessage = entry.getValue();
			long minAckId = minWaitAckMessage.getAckId();
			if (maxAckedMessage0 != null && minAckId < maxAckedMessage0.getAckId()) {// 大于最大ack
																						// id（maxAckedMessageId）的消息，不算是空洞
				if (System.currentTimeMillis() - minWaitAckMessage.gmt > WAIT_ACK_EXPIRED) {
					overdue = true;
				}
			}

			if (overdue
					&& (minWaitAckMessage = waitAckMessages0.remove(minWaitAckMessage.message.getMessageId())) != null) {// 超过阈值，则移除空洞
				if (minWaitAckMessage != null
						&& this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
					messageDao.saveMessage(this.consumerInfo.getDest().getName(), this.consumerInfo.getConsumerId(),
							minWaitAckMessage.message);
					catTraceForBackupRecord(minWaitAckMessage.message);
				}
			} else {// 没有移除任何空洞，则不再迭代；否则需要继续迭代以尽量多地移除空洞。
				break;
			}
		}
		
		
		Long ackMessageId = null;
		entry = waitAckMessages0.firstEntry();
		
		if (entry != null) {
			ackMessageId = entry.getValue().getAckId() - 1;
		}else{
			
			Long queueMaxId = getQueueEmptyMaxId(isBackup, waitAckMessages0);
			
			ackMessageId = Math.max(queueMaxId == null ? 0 : queueMaxId, maxAckedMessage0 == null ? 0L : maxAckedMessage0.getAckId());
		}

		saveAckId(ackMessageId, "batch", isBackup);
	}

	/**
	 * 主要处理消息设置filter，同时批量没有消息的情况，移动ack位置
	 * @param isBackup
	 * @param waitAckMessages0 
	 * @return
	 */
	private Long getQueueEmptyMaxId(boolean isBackup, ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages) {

		Long tailMessageId = messageQueue.getEmptyTailMessageId(isBackup);
		
		if(messageToSend.get() > 0 || !waitAckMessages.isEmpty()){
			return 0L;
		}
		
		return tailMessageId;
	}

	@Override
	public synchronized void handleChannelDisconnect(Channel channel) {
		connectedChannels.remove(channel);

		removeByChannel(channel, waitAckMessages);
		removeByChannel(channel, waitAckBackupMessages);
	}

	private void removeByChannel(Channel channel, Map<Long, ConsumerMessage> waitAckMessages0) {
		
		Iterator<Entry<Long, ConsumerMessage>> it = waitAckMessages0.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Entry<Long, ConsumerMessage> entry = (Entry<Long, ConsumerMessage>) it.next();
			ConsumerMessage consumerMessage = entry.getValue();
			if (consumerMessage.channel.equals(channel)) {
				if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
					messageDao.saveMessage(this.consumerInfo.getDest().getName(), this.consumerInfo.getConsumerId(),
							consumerMessage.message);
					catTraceForBackupRecord(consumerMessage.message);
				}
				it.remove();
			}
		}
	}

	@Override
	public boolean sendMessage() {

		final Channel channel = freeChannels.poll();
		if (channel == null || !channel.isActive()) {
			return false;
		}

		
		final SwallowMessage message = (SwallowMessage) poolMessage();
		if (message == null) {
			if (channel != null) {
				boolean result = freeChannels.offer(channel);
				if (!result) {
					logger.error("[sendMessage][channel put back error]" + consumerInfo);
				}
			}
			return false;
		}
		sendMessageExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					// 确保有消息可发
					ConsumerMessage consumerMessage = createConsumerMessage(channel, message);

					// 发消息前，验证消费者是否合法
					boolean isAuth = consumerAuthController.isValid(consumerInfo, ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
					if (!isAuth) {
						logger.error(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel)
								+ " Consumer is disabled, channel will close.");
						channel.close();
					} else {
						if (consumerMessage != null) {
							sendMessage(channel, consumerMessage);
						} else {
							freeChannels.add(channel);
						}
					}
				} catch (InterruptedException e) {
					logger.error("Get message from messageQueue thread InterruptedException", e);
				} catch (RuntimeException e) {
					logger.error("Get message from messageQueue thread Exception", e);
				}
			}
		});
		return true;
	}

	private SwallowMessage poolMessage() {
		
		messageToSend.incrementAndGet();
		
		SwallowMessage swallowMessage = messageQueue.poll();
		
		if(swallowMessage == null){
			messageToSend.decrementAndGet();
		}
		return swallowMessage;
	}

	private ConsumerMessage createConsumerMessage(Channel channel, SwallowMessage message) throws InterruptedException {
		ConsumerMessage consumerMessage = null;

		if (message != null) {
			if (!message.isBackup()) {
				consumerMessage = new ConsumerMessage(message, channel);
			} else {
				consumerMessage = new ConsumerMessage(message, channel);
			}
		}
		return consumerMessage;
	}

	private void sendMessage(Channel channel, ConsumerMessage consumerMessage) throws InterruptedException {

		PktMessage pktMessage = new PktMessage(consumerInfo.getDest(), consumerMessage.message);

		String consumerIpPort = IPUtil.getIpFromChannel(channel);

		Transaction consumerServerTransaction = Cat.getProducer().newTransaction(
				"Out:" + this.consumerInfo.getDest().getName(), consumerInfo.getConsumerId() + ":" + consumerIpPort);

		try {
			
			waitAck(consumerMessage);

			if (logger.isDebugEnabled()) {
				logger.debug("[sendMessage][channel write]");
			}

			consumerCollector.sendMessage(consumerInfo, consumerIpPort, consumerMessage.message);

			channel.writeAndFlush(pktMessage);

			consumerServerTransaction.addData("mid", pktMessage.getContent().getMessageId());
			consumerServerTransaction.setStatus(com.dianping.cat.message.Message.SUCCESS);
		} catch (RuntimeException e) {
			logger.error(consumerInfo.toString() + "：channel write error.", e);

			if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
				// 发送失败，则放到backup队列里
				messageDao.saveMessage(consumerInfo.getDest().getName(), consumerInfo.getConsumerId(),
						consumerMessage.message);
				// cat打点，记录一次备份消息的record
				catTraceForBackupRecord(consumerMessage.message);
			}
			consumerServerTransaction.addData(pktMessage.getContent().toKeyValuePairs());
			consumerServerTransaction.setStatus(e);
		} finally {
			consumerServerTransaction.complete();
		}
	}

	private void waitAck(ConsumerMessage consumerMessage) {
		
		if (!consumerMessage.message.isBackup()) {
			waitAckMessages.put(consumerMessage.message.getMessageId(), consumerMessage);
		} else {
			waitAckBackupMessages.put(consumerMessage.message.getMessageId(), consumerMessage);
		}
		
		messageToSend.decrementAndGet();
	}

	@Override
	public void handleGreet(final Channel channel, final int clientThreadCount, MessageFilter messageFilter) {
		
		setMessageFilter(messageFilter);
		
		ackExecutor.execute(new Runnable() {
			@Override
			public void run() {

				connectedChannels.putIfAbsent(channel, IPUtil.getIpFromChannel(channel));
				started = true;
				for (int i = 0; i < clientThreadCount; i++) {
					freeChannels.add(channel);
				}
			}
		});
	}

	@Override
	public void dispose() {
		
		consumerCollector.removeConsumer(consumerInfo);
		messageQueue.close();
		heartBeatReceiver.cancelCheck();
		
		for(Channel channel : connectedChannels.keySet()){
			if(channel != null){
				try{
					if(logger.isInfoEnabled()){
						logger.info("[dispose]" + channel);
					}
					channel.close();
				}catch(Exception e){
					logger.error("[dispose]" + channel, e);
				}
			}
		}
	}

	
	private void setMessageFilter(MessageFilter newFilter){
		
		MessageFilter oldFilter = this.messageFilter;
		if(oldFilter == newFilter){
			return;
		}
		
		if(oldFilter != null && !oldFilter.equals(messageFilter)){
			return;
		}
		
		
		if(logger.isInfoEnabled()){
			logger.info("[setMessageFilter][messagefilterChanged]" + oldFilter + "," + newFilter);
		}
		this.messageFilter = newFilter;
		
		updateObservers(new ConsumerConfigChanged(oldFilter, newFilter));
	}
	/**
	 * @param consumerId
	 *            consumerId为null时使用非backup队列
	 */
	private long getMaxMessageId(boolean isBakcup) {
		
		Long maxMessageId = null;
		String topicName = consumerInfo.getDest().getName();

		if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
			maxMessageId = ackDao.getMaxMessageId(topicName, consumerInfo.getConsumerId(), isBakcup);
		}

		if (maxMessageId == null) {
			maxMessageId = messageDao.getMaxMessageId(topicName, isBakcup ? consumerInfo.getConsumerId() : null);
			if (maxMessageId == null) {
				maxMessageId = MongoUtils.getLongByCurTime();
			}
			saveAckId(maxMessageId, "inited", isBakcup);
		}
		return maxMessageId;
	}

	private void saveAckId(Long ackMessageId, String desc, boolean isBakcup) {
		
		if (ackMessageId != null && ackMessageId > 0 && ackMessageId > (isBakcup ? lastRecordedBackupAckId : lastRecordedAckId)) {
			
			if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
				
				ackDao.add(consumerInfo.getDest().getName(), consumerInfo.getConsumerId(), ackMessageId, desc, isBakcup);
				
				if(!isBakcup){
					lastRecordedAckId = ackMessageId;
				}else{
					lastRecordedBackupAckId = ackMessageId;
				}
			}
		}
		
	}

	@Override
	public boolean allChannelDisconnected() {
		
		return started && connectedChannels.isEmpty();
	}

	@Override
	public ConsumerType getConsumerType() {
		return consumerInfo.getConsumerType();
	}

	/**
	 * 对消息的封装，含有其他辅助属性
	 */
	public static class ConsumerMessage {
		private SwallowMessage message;
		/** 创建时间 */
		private long gmt;

		/** 连接 */
		private Channel channel;

		public ConsumerMessage(SwallowMessage message, Channel channel) {
			super();
			this.message = message;
			this.channel = channel;
			this.gmt = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return "ConsumerMessage [message=" + message + ", gmt=" + gmt + ", channel=" + channel + "]";
		}

		public Long getAckId() {
			if (message.isBackup()) {
				return message.getBackupMessageId();
			} else {
				return message.getMessageId();
			}
		}

	}

	private void catTraceForBackupRecord(SwallowMessage message) {
		
		Transaction transaction;
		if (!message.isBackup()) {// 一条消息可能会多次放进backup队列，第一次放进backup队列的，才打点。
			transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
					"In:" + consumerInfo.getConsumerId());
		} else {
			transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
					"In-Again:" + consumerInfo.getConsumerId());
		}
		if (message != null) {
			transaction.addData("message", message.toString());
		}
		transaction.setStatus(Message.SUCCESS);
		transaction.complete();
	}

	private void catTraceForBackupAck(Long messageId, Channel channel) {
		Transaction transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
				"Ack:" + consumerInfo.getConsumerId() + ":" + IPUtil.getIpFromChannel(channel));
		if (messageId != null) {
			transaction.addData("mid", messageId);
		}
		transaction.setStatus(Message.SUCCESS);
		transaction.complete();
	}

	private void catTraceForAck(Long messageId, Channel channel) {
		Transaction transaction = Cat.getProducer().newTransaction("Ack:" + consumerInfo.getDest().getName(),
				consumerInfo.getConsumerId() + ":" + IPUtil.getIpFromChannel(channel));
		if (messageId != null) {
			transaction.addData("mid", messageId);
		}
		transaction.setStatus(Message.SUCCESS);
		transaction.complete();
	}
}
