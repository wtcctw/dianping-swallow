package com.dianping.swallow.common.internal.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午3:29:56
 */
public class ProducerMonitorData extends MonitorData{

	private Map<String, ProducerData> all = new ConcurrentHashMap<String, ProducerData>(); 
	
	
	//for json deserialize
	public ProducerMonitorData(){
		
	}
	
	public ProducerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}

	public void addData(String topic, String producerIp, long messageId, long sendTime, long saveTime){

		if(topic == null){
			logger.error("[addData][topic null]");
			topic = "";
		}
		
		if(producerIp == null){
			logger.error("[addData][producerIp null]");
			producerIp = "";
		}
		
		ProducerData ProducerData = getProducerInfo(topic);
		ProducerData.sendMessage(producerIp, messageId, sendTime, saveTime);
		
	}

	
	private ProducerData getProducerInfo(String topic) {
		
		ProducerData topicInfo = null;
		
		synchronized (topic.intern()) {
			
			topicInfo = all.get(topic);
			if(topicInfo == null){
				topicInfo = new ProducerData();
				all.put(topic, topicInfo);
			}
		}
		return topicInfo;
	}

	
	public static class ProducerData extends AbstractMonitorData{
		
		private Map<String, MessageInfo> producerMessages = new ConcurrentHashMap<String, ProducerMonitorData.MessageInfo>();
		
		public void sendMessage(String producerIp, long messageId, long sendTime, long saveTime){
			
			MessageInfo messageInfo = getMessageInfo(producerIp, producerMessages);
			messageInfo.addMessage(messageId, sendTime, saveTime);
			
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if(!(obj instanceof ProducerData)){
				return false;
			}
			ProducerData cmp = (ProducerData) obj;
			return cmp.producerMessages.equals(producerMessages);
		}
		
		@Override
		public int hashCode() {

			return producerMessages.hashCode();
		}
		
		
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)){
			return false;
		}
		if(!(obj instanceof ProducerMonitorData)){
			return false;
		}
		
		ProducerMonitorData cmp = (ProducerMonitorData) obj;
		
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
