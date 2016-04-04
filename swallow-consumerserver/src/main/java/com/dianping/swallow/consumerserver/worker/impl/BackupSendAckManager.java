package com.dianping.swallow.consumerserver.worker.impl;

import com.dianping.swallow.common.consumer.MessageFilter;
import io.netty.channel.Channel;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年11月12日 下午6:00:20
 */
public class BackupSendAckManager extends AbstractSendAckManager {

    public BackupSendAckManager(ConsumerInfo consumerInfo, SwallowBuffer swallowBuffer, MessageDAO<?> messageDao, MessageFilter messageFilter) {
        super(consumerInfo, swallowBuffer, messageDao, messageFilter);
    }

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();

        messageQueue = createMessageQueue(swallowBuffer, lastMessageId);

        if (logger.isInfoEnabled()) {
            logger.info("[doInitialize][tailMessageId]" + consumerInfo + "," + lastMessageId);
        }
    }

    @Override
    protected void catTraceForAck(ConsumerMessage consumerMessage) {

        Channel channel = consumerMessage.getChannel();
        Long messageId = consumerMessage.getAckId();

        Transaction transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
                consumerInfo.getConsumerId() + ":" + IPUtil.getIpFromChannel(channel));
        if (messageId != null) {
            transaction.addData("mid", messageId);
        }
        transaction.setStatus(Message.SUCCESS);
        transaction.complete();

    }

    @Override
    protected boolean isBackcup() {
        return true;
    }

    @Override
    protected Long getMaxMessageId() {

        return messageDao.getMaxMessageId(consumerInfo.getDest().getName(), consumerInfo.getConsumerId());
    }

    @Override
    protected CloseableBlockingQueue<SwallowMessage> createMessageQueue(SwallowBuffer swallowBuffer,
                                                                        long messageIdOfTailMessage) {

        return swallowBuffer.createBackupMessageQueue(consumerInfo, messageIdOfTailMessage);
    }

    @Override
    protected long getBeginFetchId() {
        return getMaxAckIdOrMaxMessageId();
    }

    @Override
    protected SwallowMessage doPoolMessage() {
        return messageQueue.poll();
    }

}
