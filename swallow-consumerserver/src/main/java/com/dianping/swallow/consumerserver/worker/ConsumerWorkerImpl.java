package com.dianping.swallow.consumerserver.worker;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.dianping.hawk.jmx.HawkJMXUtil;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;

/***
 * 一个ConsumerWorkerImpl负责处理一个(topic,consumerId)的消费者集群，使用单线程获取消息，并顺序推送给Consumer
 * 
 * @author kezhu.wu
 */
public final class ConsumerWorkerImpl implements ConsumerWorker {
    private static final Logger                    LOG               = LoggerFactory
                                                                             .getLogger(ConsumerWorkerImpl.class);

    private ConsumerInfo                           consumerInfo;
    private MQThreadFactory                        threadFactory;
    private String                                 consumerid;
    private String                                 topicName;
    private MessageFilter                          messageFilter;

    private AckDAO                                 ackDao;
    private SwallowBuffer                          swallowBuffer;
    private MessageDAO                             messageDao;

    private CloseableBlockingQueue<Message>        messageQueue;
    private ConfigManager                          configManager;
    private ExecutorService                        ackExecutor;
    private PullStrategy                           pullStgy;

    private volatile boolean                       getMessageisAlive = true;
    private volatile boolean                       started           = false;
    private volatile long                          maxAckedMessageId = 0L;

    //TODO 如果channel断开，未通知我们，这里会一直留着。且close时，这块也不会被处理(即未ack的消息，也不管了。如果max id比较大，那么这部分消息会被认为是成功的。)
    // 对于长期在waitAckMessages中的消息，认为没有ack，故将其放到backupQueue里，后续重新消费。
    // 重启server时，等待一段时间，对于残留在waitAckMessages里的，也将其放到backupQueue里，后续重新消费。
    /** 发送后等待ack的消息。以channel为key，每个channel可以发N条消息(N为其threadSize) */
    private Map<Channel, Map<PktMessage, Boolean>> waitAckMessages   = new ConcurrentHashMap<Channel, Map<PktMessage, Boolean>>();
    /** 待发送的消息 */
    private Queue<PktMessage>                      messagesToBeSend  = new ConcurrentLinkedQueue<PktMessage>();
    /** 可用来发送消息的channel */
    private BlockingQueue<Channel>                 freeChannels      = new LinkedBlockingQueue<Channel>();
    //TODO 是否能定时跳过channel check连接是否存活（只能设置系统的keepaliveInterval？）
    /** 存放已连接的channel，key是channel，value是ip */
    private Map<Channel, String>                   connectedChannels = new ConcurrentHashMap<Channel, String>();

    @SuppressWarnings("deprecation")
    public ConsumerWorkerImpl(ConsumerInfo consumerInfo, ConsumerWorkerManager workerManager,
                              MessageFilter messageFilter) {
        this.consumerInfo = consumerInfo;
        this.configManager = workerManager.getConfigManager();
        this.ackDao = workerManager.getAckDAO();
        this.messageDao = workerManager.getMessageDAO();
        this.swallowBuffer = workerManager.getSwallowBuffer();
        this.threadFactory = workerManager.getThreadFactory();
        this.messageFilter = messageFilter;
        topicName = consumerInfo.getConsumerId().getDest().getName();
        consumerid = consumerInfo.getConsumerId().getConsumerId();
        pullStgy = new DefaultPullStrategy(configManager.getPullFailDelayBase(),
                configManager.getPullFailDelayUpperBound());

        // consumerInfo的type不允许AT_MOST模式，遇到则修改成AT_LEAST模式（因为AT_MOST会导致ack插入比较频繁，所以不用它）
        if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_MOST_ONCE) {
            this.consumerInfo = new ConsumerInfo(consumerInfo.getConsumerId(), ConsumerType.DURABLE_AT_LEAST_ONCE);
            LOG.info("ConsumerClient[topicName=" + topicName + ", consumerid=" + consumerid
                    + "] used ConsumerType.DURABLE_AT_MOST_ONCE. Now change it to ConsumerType.DURABLE_AT_LEAST_ONCE.");
        }

        ackExecutor = new ThreadPoolExecutor(1, 1, Long.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>(),
                new MQThreadFactory("swallow-ack-"));

        start();

        //Hawk监控
        String hawkMBeanName = topicName + '-' + consumerid + "-ConsumerWorkerImpl";
        HawkJMXUtil.unregisterMBean(hawkMBeanName);
        HawkJMXUtil.registerMBean(hawkMBeanName, new HawkMBean(this));
    }

    @Override
    public void handleAck(final Channel channel, final Long ackedMsgId, final ACKHandlerType type) {
        ackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    updateWaitAckMessages(channel, ackedMsgId);
                    updateMaxMessageId(ackedMsgId, channel);
                    if (ACKHandlerType.CLOSE_CHANNEL.equals(type)) {
                        LOG.info("receive ack(type=" + type + ") from " + channel.getRemoteAddress());
                        channel.close();
                        //                  handleChannelDisconnect(channel); //channel.close()会触发netty调用handleChannelDisconnect(channel);
                    } else if (ACKHandlerType.SEND_MESSAGE.equals(type)) {
                        freeChannels.add(channel);
                    }
                } catch (Exception e) {
                    LOG.error("handleAck wrong!", e);
                }
            }
        });

    }

    //只有AT_LEAST_ONCE模式的consumer需要更新等待ack的message列表，AT_MOST_ONCE没有等待ack的message列表
    private void updateWaitAckMessages(Channel channel, Long ackedMsgId) {
        if (ConsumerType.DURABLE_AT_LEAST_ONCE.equals(consumerInfo.getConsumerType())) {
            Map<PktMessage, Boolean> messages = waitAckMessages.get(channel);
            if (messages != null) {
                SwallowMessage swallowMsg = new SwallowMessage();
                swallowMsg.setMessageId(ackedMsgId);
                PktMessage mockPktMessage = new PktMessage(consumerInfo.getConsumerId().getDest(), swallowMsg);
                messages.remove(mockPktMessage);
            }

        }

    }

    private void updateMaxMessageId(Long ackedMsgId, Channel channel) {
        if (ackedMsgId != null && ConsumerType.DURABLE_AT_LEAST_ONCE.equals(consumerInfo.getConsumerType())) {
            //         ackDao.add(topicName, consumerid, ackedMsgId, connectedChannels.get(channel));
            LOG.info("Receive ACK(" + topicName + "," + consumerid + "," + ackedMsgId + ") from "
                    + connectedChannels.get(channel));
            maxAckedMessageId = Math.max(maxAckedMessageId, ackedMsgId);
        }
    }

    @Override
    public synchronized void handleChannelDisconnect(Channel channel) {
        connectedChannels.remove(channel);
        if (ConsumerType.DURABLE_AT_LEAST_ONCE.equals(consumerInfo.getConsumerType())) {
            Map<PktMessage, Boolean> messageMap = waitAckMessages.get(channel);
            if (messageMap != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //netty自身的线程，不会有谁Interrupt。
                }
                //某个consumer断开了，那么它那些未应答的消息，就被重新放入待发送的队列里(这个时候消息顺序可能会乱序，因为序号小的被放到队列尾部了)
                for (Map.Entry<PktMessage, Boolean> messageEntry : messageMap.entrySet()) {
                    messagesToBeSend.add(messageEntry.getKey());
                }
                waitAckMessages.remove(channel);
            }
        }

    }

    private void start() {

        threadFactory.newThread(new Runnable() {

            @Override
            public void run() {
                while (getMessageisAlive) {
                    try {
                        Channel channel = freeChannels.take();
                        //如果未连接，则不做处理
                        if (channel.isConnected()) {

                            // 确保有消息可发
                            if (messagesToBeSend.isEmpty()) {
                                ensureMessagesToBoSend(channel);
                            }

                            // 拿出消息并发送
                            if (!messagesToBeSend.isEmpty()) {
                                sendMessages(channel);
                            } else {// 没有消息，channel继续放回去
                                freeChannels.add(channel);
                            }

                        }

                    } catch (InterruptedException e) {
                        LOG.info("get message from messageQueue thread InterruptedException", e);
                    }
                }
                LOG.info("message fetcher thread closed");
            }
        }, consumerInfo.toString() + "-messageFetcher-").start();

    }

    @Override
    public void handleGreet(final Channel channel, final int clientThreadCount) {
        ackExecutor.execute(new Runnable() {
            @Override
            public void run() {

                connectedChannels.put(channel, IPUtil.getIpFromChannel(channel));
                started = true;
                for (int i = 0; i < clientThreadCount; i++) {
                    freeChannels.add(channel);
                }
            }
        });
    }

    @Override
    public void closeMessageFetcherThread() {
        getMessageisAlive = false;
    }

    @Override
    public void closeAckExecutor() {
        ackExecutor.shutdownNow();
    }

    @Override
    public void close() {
        getMessageisAlive = false;
        messageQueue.close();
        //取消监控
        String hawkMBeanName = topicName + '-' + consumerid + "-ConsumerWorkerImpl";
        HawkJMXUtil.unregisterMBean(hawkMBeanName);
    }

    private long getMessageIdOfTailMessage(String topicName, String consumerId, Channel channel) {
        Long maxMessageId = null;

        //持久类型，先尝试从数据库的ack表获取最大的id
        if (consumerInfo.getConsumerType() != ConsumerType.NON_DURABLE) {
            maxMessageId = ackDao.getMaxMessageId(topicName, consumerId);
        }

        //消息id不存在，则从消息队列里获取最大消息id
        if (maxMessageId == null) {
            maxMessageId = messageDao.getMaxMessageId(topicName);

            if (maxMessageId == null) {//不存在任何消息，则使用当前时间作为消息id即可
                maxMessageId = MongoUtils.getLongByCurTime();
            }

            if (consumerInfo.getConsumerType() != ConsumerType.NON_DURABLE) {
                //持久型且ack尚未有记录，则插入ack，表示以此ack为基准。
                ackDao.add(topicName, consumerId, maxMessageId, connectedChannels.get(channel));
            }
        }
        return maxMessageId;
    }

    @SuppressWarnings("deprecation")
    private void sendMessages(Channel channel) throws InterruptedException {
        PktMessage preparedMessage = messagesToBeSend.poll();
        Long messageId = preparedMessage.getContent().getMessageId();

        //Cat begin
        Transaction consumerServerTransaction = Cat.getProducer().newTransaction("Out:" + topicName,
                consumerid + ":" + IPUtil.getIpFromChannel(channel));
        String childEventId;
        try {
            childEventId = Cat.getProducer().createMessageId();
            preparedMessage.setCatEventID(childEventId);
            Cat.getProducer().logEvent(CatConstants.TYPE_REMOTE_CALL, "ConsumedByWhom",
                    com.dianping.cat.message.Message.SUCCESS, childEventId);
        } catch (Exception e) {
            childEventId = "UnknownMessageId";
        }
        //Cat end

        try {
            channel.write(preparedMessage);
            //如果是AT_MOST模式，收到ACK之前更新messageId的类型
            if (ConsumerType.DURABLE_AT_MOST_ONCE.equals(consumerInfo.getConsumerType())) {
                ackDao.add(topicName, consumerid, messageId, connectedChannels.get(channel));
            }
            //如果是AT_LEAST模式，发送完后，在server端记录已发送但未收到ACK的消息记录
            if (ConsumerType.DURABLE_AT_LEAST_ONCE.equals(consumerInfo.getConsumerType())) {
                Map<PktMessage, Boolean> messageMap = waitAckMessages.get(channel);
                if (channel.isConnected()) {
                    if (messageMap == null) {
                        messageMap = new ConcurrentHashMap<PktMessage, Boolean>();
                        waitAckMessages.put(channel, messageMap);
                    }
                    messageMap.put(preparedMessage, Boolean.TRUE);
                } else {
                    messagesToBeSend.add(preparedMessage);
                }
            }
            //Cat begin
            consumerServerTransaction.addData("mid", preparedMessage.getContent().getMessageId());
            consumerServerTransaction.setStatus(com.dianping.cat.message.Message.SUCCESS);
            //Cat end
        } catch (RuntimeException e) {
            LOG.error(consumerInfo.toString() + "：channel write error.", e);
            messagesToBeSend.add(preparedMessage);

            //Cat begin
            consumerServerTransaction.addData(preparedMessage.getContent().toKeyValuePairs());
            consumerServerTransaction.setStatus(e);
            Cat.getProducer().logError(e);
        } finally {
            consumerServerTransaction.complete();
        }
        //Cat end
    }

    private void ensureMessagesToBoSend(Channel channel) throws InterruptedException {
        //创建消息缓冲QUEUE
        if (messageQueue == null) {
            long messageIdOfTailMessage = getMessageIdOfTailMessage(topicName, consumerid, channel);
            messageQueue = swallowBuffer.createMessageQueue(topicName, consumerid, messageIdOfTailMessage,
                    messageFilter);
        }

        SwallowMessage message = null;
        while (getMessageisAlive) {
            //从blockQueue中获取消息
            message = (SwallowMessage) messageQueue.poll(pullStgy.fail(false), TimeUnit.MILLISECONDS);
            if (message != null) {
                pullStgy.succeess();
                break;
            }
        }

        //如果因为getMessageisAlive为false而退出（如收到close命令）,则消息可能依然是null
        if (message != null) {
            messagesToBeSend.add(new PktMessage(consumerInfo.getConsumerId().getDest(), message));
        }

    }

    @Override
    public boolean allChannelDisconnected() {
        return started && connectedChannels.isEmpty();
    }

    @Override
    public long getMaxAckedMessageId() {
        return maxAckedMessageId;
    }

    @Override
    public ConsumerType getConsumerType() {
        return consumerInfo.getConsumerType();
    }

    /**
     * 用于Hawk监控
     */
    public static class HawkMBean {

        private final WeakReference<ConsumerWorkerImpl> consumerWorkerImpl;

        private HawkMBean(ConsumerWorkerImpl consumerWorkerImpl) {
            this.consumerWorkerImpl = new WeakReference<ConsumerWorkerImpl>(consumerWorkerImpl);
        }

        public String getConnectedChannels() {
            if (consumerWorkerImpl.get() != null) {
                StringBuilder sb = new StringBuilder();
                if (consumerWorkerImpl.get().connectedChannels != null) {
                    for (Channel channel : consumerWorkerImpl.get().connectedChannels.keySet()) {
                        sb.append(channel.getRemoteAddress()).append("(isConnected:").append(channel.isConnected())
                                .append(')');
                    }
                }
                return sb.toString();
            }
            return null;
        }

        public String getFreeChannels() {
            if (consumerWorkerImpl.get() != null) {
                StringBuilder sb = new StringBuilder();
                if (consumerWorkerImpl.get().freeChannels != null) {
                    for (Channel channel : consumerWorkerImpl.get().freeChannels) {
                        sb.append(channel.getRemoteAddress()).append("(isConnected:").append(channel.isConnected())
                                .append(')');
                    }
                }
                return sb.toString();
            }
            return null;
        }

        public String getConsumerInfo() {
            if (consumerWorkerImpl.get() != null) {
                return "ConsumerId=" + consumerWorkerImpl.get().consumerInfo.getConsumerId() + ",ConsumerType="
                        + consumerWorkerImpl.get().consumerInfo.getConsumerType();
            }
            return null;

        }

        //      public String getConsumerid() {
        //         return consumerid;
        //      }

        public String getTopicName() {
            if (consumerWorkerImpl.get() != null) {
                return consumerWorkerImpl.get().topicName;
            }
            return null;
        }

        public String getCachedMessages() {
            if (consumerWorkerImpl.get() != null) {
                if (consumerWorkerImpl.get().messagesToBeSend != null) {
                    return consumerWorkerImpl.get().messagesToBeSend.toString();
                }
            }
            return null;
        }

        public String getWaitAckMessages() {
            if (consumerWorkerImpl.get() != null) {
                StringBuilder sb = new StringBuilder();
                if (consumerWorkerImpl.get().waitAckMessages != null) {
                    for (Entry<Channel, Map<PktMessage, Boolean>> waitAckMessage : consumerWorkerImpl.get().waitAckMessages
                            .entrySet()) {
                        if (waitAckMessage.getValue().size() != 0) {
                            sb.append(waitAckMessage.getKey().getRemoteAddress()).append(
                                    waitAckMessage.getValue().toString());
                        }
                    }
                }
                return sb.toString();
            }
            return null;

        }

        public Boolean isGetMessageisAlive() {
            if (consumerWorkerImpl.get() != null) {
                return consumerWorkerImpl.get().getMessageisAlive;
            }
            return null;
        }

        public Boolean isStarted() {
            if (consumerWorkerImpl.get() != null) {
                return consumerWorkerImpl.get().started;
            }
            return null;
        }

        public String getMaxAckedMessageId() {
            if (consumerWorkerImpl.get() != null) {
                return Long.toString(consumerWorkerImpl.get().maxAckedMessageId);
            }
            return null;

        }

        public String getMessageFilter() {
            if (consumerWorkerImpl.get() != null) {
                if (consumerWorkerImpl.get().messageFilter != null) {
                    return consumerWorkerImpl.get().messageFilter.toString();
                }
            }
            return null;
        }

    }

}
