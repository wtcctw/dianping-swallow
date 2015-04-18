package com.dianping.swallow.common.internal.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessageUtil;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:44:42
 */
public class ConsumerMonitorData extends MonitorData{
	

	private Map<String, ConsumerTopicData>  all = new ConcurrentHashMap<String, ConsumerMonitorData.ConsumerTopicData>();
	
	
	public ConsumerMonitorData(){
		
	}
	
	public ConsumerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}
	
	
	public void addSendData(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message){
		
		if(consumerIp == null){
			logger.error("[addSendData][consumer ip null]" + consumerIp);
			consumerIp = "";
		}
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.sendMessage(consumerInfo.getConsumerId(), consumerIp, message);
				
	}

	public void addAckData(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message){
		
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.ackMessage(consumerInfo.getConsumerId(), consumerIp, message);
	}
	
	
	
	private ConsumerTopicData getConsumerTopicData(String topicName) {
		
		ConsumerTopicData consumerTopicData;
		
		synchronized (topicName.intern()) {
			
			consumerTopicData = all.get(topicName);
			if(consumerTopicData == null){
				consumerTopicData = new ConsumerTopicData();
				all.put(topicName, consumerTopicData);
			}
		}
		return consumerTopicData;
	}

	public static class ConsumerTopicData extends ConcurrentHashMap<String, ConsumerIdData>{
				
		private static final long serialVersionUID = 1L;

		public void sendMessage(String consumerId, String consumerIp, SwallowMessage message){
			
			ConsumerIdData consumerIdData = getConsumerIdData(consumerId);
			consumerIdData.sendMessage(consumerIp, message);
		}

		public void ackMessage(String consumerId, String consumerIp, SwallowMessage message) {
			ConsumerIdData consumerIdData = getConsumerIdData(consumerId);
			consumerIdData.ackMessage(consumerIp, message);
			
		}

		private ConsumerIdData getConsumerIdData(String consumerId) {
			
			ConsumerIdData consumerIdData = null;
			synchronized (consumerId.intern()) {
				
				consumerIdData = get(consumerId);
				if(consumerIdData == null){
					consumerIdData = new ConsumerIdData();
					put(consumerId, consumerIdData);
				}
			}
			return consumerIdData;
		}
	}
	

	public static class ConsumerIdData extends AbstractMonitorData{
				
		protected transient final Logger logger = LoggerFactory.getLogger(getClass());

		private Map<String, MessageInfo>  sendMessages = new ConcurrentHashMap<String, MonitorData.MessageInfo>();
		private Map<String, MessageInfo>  ackMessages = new ConcurrentHashMap<String, MonitorData.MessageInfo>();
		
		
		private Map<Long, Long>  messageSendTimes = new ConcurrentHashMap<Long, Long>();
		
		public void sendMessage(String consumerIp, SwallowMessage message){
			
			//记录消息发送时间
			messageSendTimes.put(message.getMessageId(), System.currentTimeMillis());
			
			MessageInfo messageInfo = getMessageInfo(consumerIp, sendMessages);
			
			
			long saveTime = SwallowMessageUtil.getSaveTime(message);
			if(saveTime <= 0){
				saveTime = System.currentTimeMillis();
			}
			
			messageInfo.addMessage(message.getMessageId(), saveTime, System.currentTimeMillis());
		}
		
		public void ackMessage(String consumerIp, SwallowMessage message){
			
			Long messageId = message.getMessageId();
			Long sendTime = messageSendTimes.get(messageId);
			
			if(sendTime == null){
				logger.warn("[ackMessage][unfound message]" + messageId);
				sendTime = System.currentTimeMillis();
			}
			
			try{
				MessageInfo messageInfo = getMessageInfo(consumerIp, ackMessages);
				messageInfo.addMessage(messageId, sendTime, System.currentTimeMillis());
			}finally{
				messageSendTimes.remove(messageId);
			}
		}
	
		@Override
		public boolean equals(Object obj) {
			
			if(!(obj instanceof ConsumerIdData)){
				return false;
			}
			
			ConsumerIdData cmp = (ConsumerIdData) obj;
			return cmp.sendMessages.equals(sendMessages) 
					&& cmp.ackMessages.equals(ackMessages);
		}
		
		@Override
		public int hashCode() {
			
			return (int) (sendMessages.hashCode() ^ ackMessages.hashCode());
		}

	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!super.equals(obj)){
			return false;
		}
		if(!(obj instanceof ConsumerMonitorData)){
			return false;
		}
		
		ConsumerMonitorData cmp = (ConsumerMonitorData) obj;
		return cmp.all.equals(all);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash = hash*31 + super.hashCode();
		hash = hash*31 + all.hashCode();
		return hash;
	}
}
