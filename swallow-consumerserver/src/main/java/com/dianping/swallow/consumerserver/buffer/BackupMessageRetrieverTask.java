package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

/**
 * @author mengwenchao
 * 
 *         2015年3月3日 上午10:04:19
 */
public class BackupMessageRetrieverTask extends AbstractRetrieveTask implements Runnable {

	public BackupMessageRetrieverTask(RetriveStrategy retriveStrategy, ConsumerInfo consumerInfo,
			MessageRetriever retriever, MessageBlockingQueue blockingQueue, MessageFilter messageFilter) {
		super(retriveStrategy, consumerInfo, retriever, blockingQueue, messageFilter);
	}

	
	@Override
	protected String getConsumerId() {
		return consumerInfo.getConsumerId();
	}


	@Override
	protected void setTailId(Long tailId) {
		blockingQueue.setTailBackupMessageId(tailId);
	}


	@Override
	protected Long getTailId() {
		
		return blockingQueue.getTailBackupMessageId();
	}


}