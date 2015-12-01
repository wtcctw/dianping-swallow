package com.dianping.swallow.consumerserver.buffer.impl;


import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.DefaultRetriveStrategy;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever;
import com.dianping.swallow.consumerserver.buffer.RetriveStrategy;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerConfigChanged;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerWorkerImpl;

/**
 * @author mengwenchao
 *
 * 2015年11月12日 下午7:28:19
 */
public abstract class AbstractClosableBlockingQueue extends ConcurrentLinkedQueue<SwallowMessage> implements CloseableBlockingQueue<SwallowMessage>{

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(MessageBlockingQueue.class);

	
	private final ConsumerInfo consumerInfo;

	protected transient MessageRetriever messageRetriever;

	private AtomicBoolean isClosed = new AtomicBoolean(false);

	private AtomicInteger checkSendMessageSize = new AtomicInteger();

	protected MessageFilter messageFilter;
	
	/** 最小剩余数量,当queue的消息数量小于threshold时，会触发从数据库加载数据的操作 */
	private final int minThreshold;
	private final int maxThreshold;

	protected volatile Long tailMessageId;

	private ExecutorService retrieverThreadPool;

	private RetriveStrategy retriveStrategy;
	
	private Object getTailMessageIdLock = new Object();

	
	public AbstractClosableBlockingQueue(ConsumerInfo consumerInfo, MessageFilter messageFilter, int minThreshold,
			int maxThreshold, int capacity, Long messageIdOfTailMessage, ExecutorService retrieverThreadPool) {

		this.consumerInfo = consumerInfo;
		this.messageFilter = messageFilter;
		if (minThreshold < 0 || maxThreshold < 0 || minThreshold > maxThreshold) {
			throw new IllegalArgumentException("wrong threshold: "
					+ minThreshold + "," + maxThreshold);
		}
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThreshold;
		if (messageIdOfTailMessage == null) {
			throw new IllegalArgumentException("messageIdOfTailMessage is null.");
		}
		this.tailMessageId = messageIdOfTailMessage;
		this.retrieverThreadPool = retrieverThreadPool;

		this.retriveStrategy = new DefaultRetriveStrategy(consumerInfo, ConfigManager.getInstance().getMinRetrieveInterval(), this.maxThreshold, 
				ConfigManager.getInstance().getMaxRetriverTaskCountPerConsumer());
	}

	
	@Override
	public SwallowMessage poll() {

		ensureLeftMessage();
		
		if(logger.isDebugEnabled() && size() >= maxThreshold){
			logger.debug("[poll]" + size());
		}

		SwallowMessage message = super.poll();
		decreaseMessageCount(message);
		return message;
	}

	@Override
	public SwallowMessage peek() {
		
		ensureLeftMessage();
		SwallowMessage message = super.peek();
		return message;
	}


	
	private void decreaseMessageCount(SwallowMessage message) {
		if(message != null){
			retriveStrategy.decreaseMessageCount();
			checkSendMessageSize.incrementAndGet();
		}
	}

	private void increaseMessageCount() {
		retriveStrategy.increaseMessageCount();
	}

	/**
	 * 唤醒“获取DB数据的后台线程”去DB获取数据，并添加到Queue的尾部
	 */
	private void ensureLeftMessage() {

		if (retriveStrategy.messageCount() < minThreshold) {

			if(retriveStrategy.canPutNewTask()){
				retrieverThreadPool.execute(createMessageRetrieverTask(retriveStrategy, consumerInfo, messageRetriever, this, messageFilter));
				retriveStrategy.offerNewTask();
			}
		}
	}

	protected abstract Runnable createMessageRetrieverTask(RetriveStrategy retriveStrategy, ConsumerInfo consumerInfo,
			MessageRetriever messageRetriever, AbstractClosableBlockingQueue abstractClosableBlockingQueue,
			MessageFilter messageFilter);


	public void setMessageRetriever(MessageRetriever messageRetriever) {
		this.messageRetriever = messageRetriever;
	}

	@Override
	public void close() {
		if (isClosed.compareAndSet(false, true)) {
		}
	}

	@Override
	public void isClosed() {
		if (isClosed.get()) {
			throw new RuntimeException("MessageBlockingQueue- already closed! ");
		}
	}

	public void putMessage(List<SwallowMessage> messages) {
		
		for (SwallowMessage message : messages) {
			
			boolean result = offer(message);
			if(result){
				increaseMessageCount();
			}else{
				//TODO
				logger.warn("[putMessage][fail]");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Add message to (topic=" + consumerInfo.getDest().getName() + ",cid=" + consumerInfo.getConsumerId() + ") queue:" + message.toString());
			}
		}
	}


	public Long getTailMessageId() {
		return tailMessageId;
	}

	public void setTailMessageId(Long tailMessageId) {
		
		synchronized (getTailMessageIdLock) {
			
			this.tailMessageId = tailMessageId;
		}
	}

	@Override
	public Long getEmptyTailMessageId() {

		synchronized (getTailMessageIdLock) {
			
			if(isEmpty()){
				
				return getTailMessageId();
			}
		}
		
		return null;
	}

	@Override
	public void update(Observable observable, Object args) {
		
		if(!(observable instanceof ConsumerWorkerImpl)){
			throw new IllegalArgumentException("observable not supported!" + observable.getClass());
		}
		
		ConsumerConfigChanged changed = (ConsumerConfigChanged) args;
		
		switch (changed.getConsumerConfigChangeType()) {
		
			case MESSAGE_FILTER:
				this.messageFilter = changed.getNewMessageFilter();
				break;
			default:
				throw new IllegalArgumentException("type not supported!" + changed.getConsumerConfigChangeType());
		}
		
	}
}
