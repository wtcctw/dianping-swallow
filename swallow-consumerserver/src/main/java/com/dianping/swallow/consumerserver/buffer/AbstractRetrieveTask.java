package com.dianping.swallow.consumerserver.buffer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

/**
 * @author mengwenchao
 *
 * 2015年3月3日 上午10:02:15
 */
public abstract class AbstractRetrieveTask implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected RetriveStrategy retriveStrategy;
	protected ConsumerInfo consumerInfo;
	protected MessageRetriever messageRetriever;
	protected MessageBlockingQueue blockingQueue;
	protected MessageFilter messageFilter;

	public AbstractRetrieveTask(RetriveStrategy retriveStrategy, ConsumerInfo consumerInfo, MessageRetriever messageRetriever, 
			MessageBlockingQueue blockingQueue, MessageFilter messageFilter) {
		this.retriveStrategy = retriveStrategy;
		this.consumerInfo = consumerInfo;
		this.messageRetriever = messageRetriever;
		this.blockingQueue = blockingQueue;
		this.messageFilter = messageFilter;
	}

	@Override
	public void run() {
		
		synchronized (retriveStrategy) {
			try {
				retriveStrategy.beginRetrieve();
				if (retriveStrategy.isRetrieve() && messageRetriever != null) {
					retrieveMessage();
				}
			} catch (Throwable th) {
				logger.error("[run]" + consumerInfo, th);
			}finally{
				retriveStrategy.endRetrieve();
			}
		}
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "@" + this.hashCode() + "," + consumerInfo+ "]";
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		if(!(obj.getClass().equals(getClass()))){
			return false;
		}
		AbstractRetrieveTask cmp = (AbstractRetrieveTask) obj;
		return cmp.consumerInfo.equals(consumerInfo);
	}
	
	@Override
	public int hashCode() {
		return consumerInfo.hashCode();
	}

	@SuppressWarnings("rawtypes")
	protected void updateRetrieveStrategy(List messages, Long tailId) {
		int messageSize = messages == null ? 0 : messages.size();
		if (logger.isInfoEnabled() && messageSize > 0) {
			logger.info("[updateRetrieveStrategy][read message]" + consumerInfo + "," + tailId + "," + messageSize);
		}
		retriveStrategy.retrieved(messageSize);
	}

	@SuppressWarnings("rawtypes")
	protected void retrieveMessage() {

		if (logger.isDebugEnabled()) {
			logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
		}

		List messages = messageRetriever.retrieveMessage(consumerInfo.getDest().getName(),
				getConsumerId(), getTailId(), messageFilter);
		updateRetrieveStrategy(messages, getTailId());
		if (messages != null && messages.size() > 0) {
			setTailId((Long) messages.get(0));
			putMessage(messages);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
		}
	}

	protected void putMessage(@SuppressWarnings("rawtypes") List messages) {
		blockingQueue.putMessage(messages);;
	}

	protected abstract void setTailId(Long tailId);

	protected abstract String getConsumerId();

	protected abstract Long getTailId();



}