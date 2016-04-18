package com.dianping.swallow.consumerserver.worker.impl;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.observer.Observable;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.worker.SendAckManager;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年11月12日 下午5:07:26
 */
public abstract class AbstractSendAckManager extends AbstractLifecycle implements SendAckManager {

    private final long WAIT_ACK_EXPIRED = ConfigManager.getInstance().getWaitAckExpiredSecond() * 1000;

    protected volatile CloseableBlockingQueue<SwallowMessage> messageQueue;

    private volatile ConsumerMessage maxAckedMessage;

    private volatile long lastRecordedAckId = 0L;

    protected MessageDAO<?> messageDao;

    protected ConsumerInfo consumerInfo;

    private ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages = new ConcurrentSkipListMap<Long, ConsumerMessage>();

    protected AtomicInteger messageToSend = new AtomicInteger();

    protected SwallowBuffer swallowBuffer;

    protected volatile long lastMessageId;

    private volatile MessageFilter messageFilter;

    public AbstractSendAckManager(ConsumerInfo consumerInfo, SwallowBuffer swallowBuffer, MessageDAO<?> messageDao, MessageFilter messageFilter) {

        this.consumerInfo = consumerInfo;
        this.swallowBuffer = swallowBuffer;
        this.messageDao = messageDao;
        this.messageFilter = messageFilter;
    }


    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();

        long idOfTailMessage = getBeginFetchId();
        lastMessageId = idOfTailMessage;
    }

    @Override
    protected void doDispose() throws Exception {
        super.doDispose();
        if (messageQueue != null) {
            messageQueue.close();
        }
    }

    protected abstract long getBeginFetchId();

    /**
     * 有ackid，获取最大ackid；无则获取最大消息id
     *
     * @return
     */
    protected long getMaxAckIdOrMaxMessageId() {

        Long maxMessageId = null;

        String topicName = consumerInfo.getDest().getName();

        if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {

            maxMessageId = messageDao.getAckMaxMessageId(topicName, consumerInfo.getConsumerId(), isBackcup());
            if (logger.isInfoEnabled() && maxMessageId != null) {
                logger.info("[getMaxAckIdOrMaxMessageId][use AckMaxMessageId]" + consumerInfo);
            }
        }

        if (maxMessageId == null) {

            maxMessageId = getMaxMessageId();
            if (maxMessageId == null) {
                maxMessageId = messageDao.getMessageEmptyAckId(consumerInfo.getDest().getName());
                if (logger.isInfoEnabled()) {
                    logger.info("[getMaxAckIdOrMaxMessageId][use MessageEmptyAckId]" + consumerInfo);
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("[getMaxAckIdOrMaxMessageId][use MaxMessageId]" + consumerInfo);
                }
            }

            if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
                saveAckId(maxMessageId, "inited");
            }
        }
        return maxMessageId;
    }

    protected abstract Long getMaxMessageId();

    @Override
    public ConsumerMessage send() {
        return poolMessage();
    }


    @Override
    public void exceptionWhileSending(ConsumerMessage consumerMessage, Throwable th) {

        backupMessage(consumerMessage, "exception" + th.getMessage());
    }

    @Override
    public ConsumerMessage ack(Long ackId) {

        ConsumerMessage waitAckMessage = waitAckMessages.remove(ackId);

        if (waitAckMessage == null) {
            return null;
        }

        if (maxAckedMessage == null || waitAckMessage.getAckId() > maxAckedMessage.getAckId()) {
            maxAckedMessage = waitAckMessage;
        }

        catTraceForAck(waitAckMessage);

        return waitAckMessage;
    }

    protected abstract void catTraceForAck(ConsumerMessage consumerMessage);

    @Override
    public void recordAck() {

        Entry<Long, ConsumerMessage> entry;

        while ((entry = waitAckMessages.firstEntry()) != null) {// 使用while，尽最大可能消除空洞ack
            // id
            boolean overdue = false;// 是否超过阈值

            ConsumerMessage minWaitAckMessage = entry.getValue();
            Long minAckId = entry.getKey();

            if (maxAckedMessage != null && minAckId < maxAckedMessage.getAckId()) {// 大于最大ack
                // id（maxAckedMessageId）的消息，不算是空洞
                if (System.currentTimeMillis() - minWaitAckMessage.getGmt() > WAIT_ACK_EXPIRED) {
                    overdue = true;
                }
            }

            if (overdue) {
                backupMessage(minWaitAckMessage, "ack timeout");
            } else {// 没有移除任何空洞，则不再迭代；否则需要继续迭代以尽量多地移除空洞。
                break;
            }
        }

        Long ackMessageId = null;
        entry = waitAckMessages.firstEntry();

        if (entry != null) {
            ackMessageId = entry.getValue().getAckId() - 1;
        } else {

            Long queueMaxId = getQueueEmptyMaxId(waitAckMessages);

            Long ackQueue = queueMaxId == null ? 0 : queueMaxId;
            Long ackMaxMessage = maxAckedMessage == null ? 0L : maxAckedMessage.getAckId();

            ackMessageId = Math.max(ackQueue, ackMaxMessage);

            if (ackQueue > ackMaxMessage && ackQueue > lastRecordedAckId) {
                if (logger.isInfoEnabled()) {
                    logger.info("[recordAck][use queueMaxId]" + ackQueue + "," + consumerInfo);
                }
            }
        }

        saveAckId(ackMessageId, "batch");
    }


    @Override
    public void destClosed(Channel channel) {
        removeByChannel(channel, waitAckMessages);
    }

    private void removeByChannel(Channel channel, Map<Long, ConsumerMessage> waitAckMessages0) {

        Iterator<Entry<Long, ConsumerMessage>> it = waitAckMessages0.entrySet().iterator();

        while (it.hasNext()) {

            Entry<Long, ConsumerMessage> entry = (Entry<Long, ConsumerMessage>) it.next();
            ConsumerMessage consumerMessage = entry.getValue();

            if (consumerMessage.getChannel().equals(channel)) {
                backupMessage(consumerMessage, "channel removed:" + channel);
            }
        }
    }


    private ConsumerMessage poolMessage() {

        messageToSend.incrementAndGet();
        SwallowMessage swallowMessage = null;

        try {
            swallowMessage = doPoolMessage();
        } catch (Exception e) {
            logger.error("[poolMessage] doPoolMessage failed.", e);
        }

        if (swallowMessage == null) {
            messageToSend.decrementAndGet();
            return null;
        } else {
            lastMessageId = swallowMessage.getMessageId();

            if (MessageFilter.isFiltered(messageFilter, swallowMessage.getType())) {
                messageToSend.decrementAndGet();
                return null;
            }
        }

        ConsumerMessage consumerMessage = createConsumerMessage(swallowMessage);

        waitAck(consumerMessage);

        return consumerMessage;
    }

    protected abstract SwallowMessage doPoolMessage();

    protected void waitAck(ConsumerMessage consumerMessage) {

        waitAckMessages.put(consumerMessage.getAckId(), consumerMessage);

        messageToSend.decrementAndGet();
    }


    protected ConsumerMessage createConsumerMessage(SwallowMessage message) {

        ConsumerMessage consumerMessage = null;

        if (message != null) {
            consumerMessage = new ConsumerMessage(message, this);
        }
        return consumerMessage;
    }

    private void backupMessage(final ConsumerMessage consumerMessage, String reason) {

        waitAckMessages.remove(consumerMessage.getAckId());

        if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {

            String desc = String.format("[%d][%s]", isBackcup() ? 1 : 0, reason);


            if (logger.isInfoEnabled()) {
                logger.info("[backupMessage]" + desc + "," + consumerInfo + "," + consumerMessage);
            }

            SwallowActionWrapper actionWrapper = new CatActionWrapper("Backup:" + consumerInfo.getDest().getName(), desc + consumerInfo.getConsumerId());

            actionWrapper.doAction(new SwallowAction() {

                @Override
                public void doAction() throws SwallowException {

                    messageDao.saveMessage(consumerInfo.getDest().getName(), consumerInfo.getConsumerId(), consumerMessage.getMessage());
                }
            });
        }
    }


    /**
     * 主要处理消息设置filter，同时批量没有消息的情况，移动ack位置
     *
     * @return
     */
    protected Long getQueueEmptyMaxId(ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages) {

        Long tailMessageId = messageQueue.getEmptyTailMessageId();

        if (messageToSend.get() > 0 || !waitAckMessages.isEmpty()) {
            return 0L;
        }

        return tailMessageId;
    }

    private void saveAckId(Long ackMessageId, String desc) {

        if (ackMessageId != null && ackMessageId > 0 && ackMessageId > lastRecordedAckId) {

            if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {

                messageDao.addAck(consumerInfo.getDest().getName(), consumerInfo.getConsumerId(), ackMessageId, desc, isBackcup());
                lastRecordedAckId = ackMessageId;
            }
        }
    }

    protected abstract boolean isBackcup();

    protected abstract CloseableBlockingQueue<SwallowMessage> createMessageQueue(SwallowBuffer swallowBuffer, long messageIdOfTailMessage);

    @Override
    public String toString() {

        return getClass().getSimpleName() + ":" + consumerInfo.toString();
    }

    @Override
    public void update(Observable observable, Object args) {

        if (!(observable instanceof ConsumerWorkerImpl)) {
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
