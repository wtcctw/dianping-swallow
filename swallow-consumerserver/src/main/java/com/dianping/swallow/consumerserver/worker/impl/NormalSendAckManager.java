package com.dianping.swallow.consumerserver.worker.impl;

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
 *
 * 2015年11月12日 下午5:57:26
 */
public class NormalSendAckManager extends AbstractSendAckManager{
	
	private long startMessageId;
	
	public NormalSendAckManager(ConsumerInfo consumerInfo, SwallowBuffer swallowBuffer, MessageDAO<?> messageDao, long startMessageId) {
		
		super(consumerInfo, swallowBuffer, messageDao);
		this.startMessageId = startMessageId;
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
	protected Transaction createBakupTranaction() {
		
		return Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
				"In:" + consumerInfo.getConsumerId());
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
	protected CloseableBlockingQueue<SwallowMessage> createMessageQueue(SwallowBuffer swallowBuffer,
			long messageIdOfTailMessage) {
		return swallowBuffer.createMessageQueue(consumerInfo, messageIdOfTailMessage);
	}

	@Override
	protected long getBeginFetchId() {

		return startMessageId >= 0 ? startMessageId : getMaxAckIdOrMaxMessageId();
	}


}
