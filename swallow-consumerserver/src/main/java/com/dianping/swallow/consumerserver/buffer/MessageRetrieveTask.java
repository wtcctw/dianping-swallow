package com.dianping.swallow.consumerserver.buffer;


import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.consumerserver.buffer.impl.MessageBlockingQueue;

/**
 * @author mengwenchao
 * 
 *         2015年3月3日 上午10:16:43
 */
public class MessageRetrieveTask extends BlockingQueueRetrieveTask implements Runnable {

	public MessageRetrieveTask(RetrieveStrategy retrieveStrategy, ConsumerInfo consumerInfo,
							   MessageRetriever messageRetriever, MessageBlockingQueue blockingQueue, MessageFilter messageFilter) {
		super(retrieveStrategy, consumerInfo, messageRetriever, blockingQueue, messageFilter);
	}

	@Override
	protected String getConsumerId() {
		return null;
	}



}