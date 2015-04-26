package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessageUtil;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTotalMap;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfoTotalMap;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:44:42
 */
@Document( collection = "ConsumerMonitorData")
public class ConsumerMonitorData extends MonitorData{
	

	private ConsumerTotalMap  all = new ConsumerTotalMap();
	
	
	public ConsumerMonitorData(){
		
	}
	
	public ConsumerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}
	
	@Override
	protected void doMerge(MonitorData mergeData) {
		
		if(!(mergeData instanceof ConsumerMonitorData)){
			throw new IllegalArgumentException("mergeData type not right " + mergeData.getClass());
		}
		
		ConsumerMonitorData consumerMergeData = (ConsumerMonitorData) mergeData;
		all.merge(consumerMergeData.all);
	}
	
	
	@Override
	protected Mergeable getTopic(KeyMergeable merge, String topic) {
		
		ConsumerMonitorData cmd = (ConsumerMonitorData) merge;
		return cmd.getTopic(topic);
	}

	@Override
	protected Mergeable getTopic(String topic) {
		
		return MapUtil.getOrCreate(all, topic, ConsumerTopicData.class);
	}
	
	public void addSendData(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message){
		
		if(consumerIp == null){
			logger.error("[addSendData][consumer ip null]" + consumerIp);
			consumerIp = "";
		}
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.sendMessage(consumerInfo.getConsumerId(), consumerIp, message);
		
		all.getTotal().sendMessage(TOTAL_KEY, TOTAL_KEY, message);
				
	}

	public void addAckData(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message){
		
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.ackMessage(consumerInfo.getConsumerId(), consumerIp, message);
		
		all.getTotal().ackMessage(TOTAL_KEY, TOTAL_KEY, message);
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

	public static class ConsumerTopicData extends TotalMap<ConsumerIdData>{
				
		private static final long serialVersionUID = 1L;

		public void sendMessage(String consumerId, String consumerIp, SwallowMessage message){
			
			ConsumerIdData consumerIdData = getConsumerIdData(consumerId);
			consumerIdData.sendMessage(consumerIp, message);
			
			total.sendMessage(TOTAL_KEY, message);
		}

		public void merge(ConsumerTopicData consumerTopicData) {
			
			for(Entry<String, ConsumerIdData> entry : entrySet()){
				
				String key = entry.getKey();
				ConsumerIdData value = entry.getValue();
				
				value.merge(consumerTopicData.get(key));
			}
		}

		public void ackMessage(String consumerId, String consumerIp, SwallowMessage message) {
			
			ConsumerIdData consumerIdData = getConsumerIdData(consumerId);
			consumerIdData.ackMessage(consumerIp, message);
			total.ackMessage(consumerIp, message);
			
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

		@Override
		protected ConsumerIdData createValue() {
			
			return new ConsumerIdData();
		}
	}
	

	public static class ConsumerIdData implements KeyMergeable{
				
		@Transient
		protected transient final Logger logger = LoggerFactory.getLogger(getClass());

		private MessageInfoTotalMap  sendMessages  = new MessageInfoTotalMap();
		private MessageInfoTotalMap  ackMessages  = new MessageInfoTotalMap();
		
		@Transient
		private Map<Long, Long>  messageSendTimes = new ConcurrentHashMap<Long, Long>();
		
		public ConsumerIdData(){
						
		}
		
		@Transient
		public MessageInfo getTotalSendMessages(){
			return sendMessages.getTotal();
		}

		@Transient
		public MessageInfo getTotalAckMessages(){
			return ackMessages.getTotal();
				
		}

		public void merge(Mergeable merge) {
			
			checkType(merge);
			
			ConsumerIdData toMerge = (ConsumerIdData) merge;
			sendMessages.merge(toMerge.sendMessages);
			ackMessages.merge(toMerge.ackMessages);
		}
		
		private void checkType(Mergeable merge) {
			if(!(merge instanceof ConsumerIdData)){
				throw new IllegalArgumentException("wrong type " + merge.getClass());
			}
		}

		@Override
		public void merge(String key, KeyMergeable merge) {
			
			checkType(merge);
			
			ConsumerIdData toMerge = (ConsumerIdData) merge;
			sendMessages.merge(key, toMerge.sendMessages);
			ackMessages.merge(key, toMerge.ackMessages);
		}



		public void sendMessage(String consumerIp, SwallowMessage message){
			
			//记录消息发送时间
			messageSendTimes.put(message.getMessageId(), System.currentTimeMillis());
			
			MessageInfo messageInfo = MapUtil.getOrCreate(sendMessages, consumerIp, MessageInfo.class);
			
			long saveTime = SwallowMessageUtil.getSaveTime(message);
			if(saveTime <= 0){
				saveTime = System.currentTimeMillis();
			}
			messageInfo.addMessage(message.getMessageId(), saveTime, System.currentTimeMillis());
			
			sendMessages.getTotal().addMessage(message.getMessageId(), saveTime, System.currentTimeMillis());
		}
		
		public void ackMessage(String consumerIp, SwallowMessage message){
			
			Long messageId = message.getMessageId();
			Long sendTime = messageSendTimes.get(messageId);
			
			if(sendTime == null){
				logger.warn("[ackMessage][unfound message]" + messageId + "," + this);
				sendTime = System.currentTimeMillis();
			}
			
			try{
				MessageInfo messageInfo = MapUtil.getOrCreate(ackMessages, consumerIp, MessageInfo.class);
				messageInfo.addMessage(messageId, sendTime, System.currentTimeMillis());
				
				ackMessages.getTotal().addMessage(messageId, sendTime, System.currentTimeMillis());
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
		public String toString() {
			return JsonBinder.getNonEmptyBinder().toJson(this);
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

	@Override
	protected TotalMap<?> getTopicData(String topic) {
		
		return all.get(topic);
	}


}
