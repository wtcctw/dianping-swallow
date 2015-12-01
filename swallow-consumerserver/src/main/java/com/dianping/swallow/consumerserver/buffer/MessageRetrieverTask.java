package com.dianping.swallow.consumerserver.buffer;


import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.consumerserver.buffer.impl.MessageBlockingQueue;

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
	protected String getConsumerId() {
		return null;
	}



}