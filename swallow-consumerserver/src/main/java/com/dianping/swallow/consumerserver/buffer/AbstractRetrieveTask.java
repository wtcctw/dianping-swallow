package com.dianping.swallow.consumerserver.buffer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever.ReturnMessageWrapper;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;

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
	protected CloseableBlockingQueue<SwallowMessage> blockingQueue;
	protected MessageFilter messageFilter;

	public AbstractRetrieveTask(RetriveStrategy retriveStrategy, ConsumerInfo consumerInfo, MessageRetriever messageRetriever, 
			CloseableBlockingQueue<SwallowMessage> blockingQueue, MessageFilter messageFilter) {
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

	protected void updateRetrieveStrategy(int rawMessageSize, List<SwallowMessage> messages, Long tailId) {
		
		if (logger.isDebugEnabled() && rawMessageSize > 0) {
			
			logger.debug(String.format("[updateRetrieveStrategy][read message]%s,%d,%d", consumerInfo, tailId, rawMessageSize));
			
			int messageSize = messages == null ? 0 : messages.size();
			if(messageSize != rawMessageSize){
				logger.debug("[updateRetrieveStrategy][real message size]" + messageSize);
			}
		}
		
		retriveStrategy.retrieved(rawMessageSize);
	}

	protected void retrieveMessage() {

		if (logger.isDebugEnabled()) {
			logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
		}

		ReturnMessageWrapper messageWrapper = messageRetriever.retrieveMessage(consumerInfo.getDest().getName(),
				getConsumerId(), getTailId(), messageFilter);
		
		List<SwallowMessage> messages = messageWrapper.getMessages();
		
		updateRetrieveStrategy(messageWrapper.getRawMessageSize(), messageWrapper.getMessages(), getTailId());
		
		putMessage(messages);

		if(messageWrapper.getRawMessageSize() > 0){
			setTailId(messageWrapper.getMaxMessageId());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
		}
	}

	protected void putMessage(List<SwallowMessage> messages) {
		
		blockingQueue.putMessage(messages);
		
		if(EnvUtil.isQa()){
			blockingQueue.putMessage(messages);
		}
	}

	protected void setTailId(Long tailId) {
		blockingQueue.setTailMessageId(tailId);
	}

	protected Long getTailId() {
		return blockingQueue.getTailMessageId();
	}

	protected abstract String getConsumerId();

}