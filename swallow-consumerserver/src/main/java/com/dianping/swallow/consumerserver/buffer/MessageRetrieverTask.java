package com.dianping.swallow.consumerserver.buffer;


import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

/**
 * @author mengwenchao
 * 
 *         2015年3月3日 上午10:16:43
 */
public class MessageRetrieverTask extends AbstractRetrieveTask implements Runnable {

	public MessageRetrieverTask(RetriveStrategy retriveStrategy, ConsumerInfo consumerInfo,
			MessageRetriever messageRetriever, MessageBlockingQueue blockingQueue, MessageFilter messageFilter) {
		super(retriveStrategy, consumerInfo, messageRetriever, blockingQueue, messageFilter);
	}

	@Override
	protected void setTailId(Long tailId) {
		blockingQueue.setTailMessageId(tailId);
	}

	@Override
	protected String getConsumerId() {
		return null;
	}

	@Override
	protected Long getTailId() {
		return blockingQueue.getTailMessageId();
	}


}