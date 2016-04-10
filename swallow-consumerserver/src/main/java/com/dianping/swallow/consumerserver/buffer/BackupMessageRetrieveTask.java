package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 * 
 *         2015年3月3日 上午10:04:19
 */
public class BackupMessageRetrieveTask extends BlockingQueueRetrieveTask implements Runnable {

	public BackupMessageRetrieveTask(RetrieveStrategy retrieveStrategy, ConsumerInfo consumerInfo,
									 MessageRetriever retriever, CloseableBlockingQueue<SwallowMessage> blockingQueue, MessageFilter messageFilter) {
		super(retrieveStrategy, consumerInfo, retriever, blockingQueue, messageFilter);
	}

	
	@Override
	protected String getConsumerId() {
		return consumerInfo.getConsumerId();
	}

}