package com.dianping.swallow.consumerserver.worker.impl;

import com.dianping.swallow.common.internal.exception.SwallowIOException;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.consumerserver.buffer.*;
import com.dianping.swallow.consumerserver.buffer.impl.MessageRingBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import io.netty.channel.Channel;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年11月12日 下午5:57:26
 */
public class NormalSendAckManager extends AbstractSendAckManager {

    private MessageRingBuffer.BufferReader bufferReader;

    private CloseableRingBuffer<SwallowMessage> messageBuffer;

    private long startMessageId;

    private AtomicBoolean isBuffer = new AtomicBoolean(false);

    private SwitchStrategy switchStrategy;

    private volatile long switchCount = 0;

    private volatile long trySwitchCount = 0;

    public NormalSendAckManager(ConsumerInfo consumerInfo, SwallowBuffer swallowBuffer, MessageDAO<?> messageDao, long startMessageId) {

        super(consumerInfo, swallowBuffer, messageDao);
        this.startMessageId = startMessageId;
        switchStrategy = new DefaultSwitchStrategy(ConfigManager.getInstance().getMinSwitchInterval(),
                ConfigManager.getInstance().getMaxSwitchInterval(), ConfigManager.getInstance().getSwitchTimeUnit());

    }

    @Override
    protected void doInitialize() throws Exception {

        super.doInitialize();
        messageBuffer = swallowBuffer.getOrCreateMessageBuffer(consumerInfo);
        this.bufferReader = messageBuffer.getOrCreateReader(consumerInfo.getConsumerId());

        if (bufferReader.tryOpen(lastMessageId).isOpen()) {
            isBuffer.compareAndSet(false, true);
        } else {
            messageQueue = createMessageQueue(swallowBuffer, lastMessageId);
            isBuffer.compareAndSet(true, false);
        }

        if (logger.isInfoEnabled()) {
            logger.info("[doInitialize][tailMessageId]" + consumerInfo + "," + lastMessageId + " , isBuffer " + isBuffer.get());
        }
    }

    protected void catTraceForAck(ConsumerMessage consumerMessage) {

        Channel channel = consumerMessage.getChannel();
        Long messageId = consumerMessage.getAckId();

        Transaction transaction = Cat.getProducer().newTransaction("Ack:" + consumerInfo.getDest().getName(),
                consumerInfo.getConsumerId() + ":" + IPUtil.getIpFromChannel(channel));

        if (messageId != null) {
            transaction.addData("mid", messageId);
        }
        transaction.setStatus(Message.SUCCESS);
        transaction.complete();
    }

    @Override
    protected SwallowMessage doPoolMessage() {

        SwallowMessage message = null;
        messageBuffer.fetchMessage(bufferReader, lastMessageId);

        if (isBuffer.get()) {
            try {
                message = this.bufferReader.next();
            } catch (SwallowIOException e) {
                logger.warn("[poolMessage0] " + consumerInfo + ", lastMessageId " + lastMessageId, e);

                messageQueue = createMessageQueue(swallowBuffer, lastMessageId);
                isBuffer.compareAndSet(true, false);
                switchStrategy.switched(MessageRingBuffer.ReaderStatus.CLOSED_BACK);
            }
        } else {
            if (switchStrategy.isSwitch()) {
                trySwitchCount++;

                MessageRingBuffer.ReaderStatus status = bufferReader.tryOpen(lastMessageId);
                if (status.isOpen()) {
                    logger.warn("[poolMessage0] switch success, " + consumerInfo);
                    switchCount++;
                    isBuffer.compareAndSet(false, true);
                    messageQueue = null;
                }

                switchStrategy.switched(status);
            } else {
                message = messageQueue.poll();
            }
        }
        return message;
    }


    @Override
    protected boolean isBackcup() {
        return false;
    }

    @Override
    protected Long getMaxMessageId() {

        return messageDao.getMaxMessageId(consumerInfo.getDest().getName(), null);
    }

    @Override
    protected Long getQueueEmptyMaxId(ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages) {

        Long tailMessageId;
        if (messageQueue != null) {
            tailMessageId = messageQueue.getEmptyTailMessageId();
        } else {
            tailMessageId = bufferReader.getEmptyTailMessageId();
        }

        if (messageToSend.get() > 0 || !waitAckMessages.isEmpty()) {
            return 0L;
        }

        return tailMessageId;
    }

    @Override
    protected CloseableBlockingQueue<SwallowMessage> createMessageQueue(SwallowBuffer swallowBuffer,
                                                                        long messageIdOfTailMessage) {
        return swallowBuffer.createMessageQueue(consumerInfo, messageIdOfTailMessage);
    }

    @Override
    protected long getBeginFetchId() {

        return startMessageId >= 0 ? startMessageId : getMaxAckIdOrMaxMessageId();
    }

    @Override
    public void update(Observable observable, Object args) {
        if (messageQueue != null) {
            messageQueue.update(observable, args);
        }
        bufferReader.update(observable, args);
    }


    @Override
    public String toString() {
        return "NormalSendAckManager[bufferReader=" + bufferReader + ", messageBuffer=" + messageBuffer +
                ", startMessageId=" + startMessageId + ", lastMessageId=" + lastMessageId + ", isBuffer=" + isBuffer +
                ", switchCount=" + switchCount + ", trySwitchCount=" + trySwitchCount + '}';
    }
}
