package com.dianping.swallow.common.server.monitor.data;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.MapUtil;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午3:29:56
 */
@Document( collection = "ProducerMonitorData")
public class ProducerMonitorData extends MonitorData{

	private TotalConcurrentHashMap<String, ProducerData> all; 
	
	//for json deserialize
	public ProducerMonitorData(){
		this(IPUtil.getFirstNoLoopbackIP4Address());
	}
	
	public ProducerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
		
		all = new TotalConcurrentHashMap<String, ProducerMonitorData.ProducerData>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected ProducerData createValue() {
				return new ProducerData();
			}
		};
	}
	
	
	@Override
	protected void doMerge(MonitorData mergeData) {
		
		if(!(mergeData instanceof ProducerMonitorData)){
			throw new IllegalArgumentException("wrong type " + mergeData.getClass());
		}
		
		ProducerMonitorData toMerge = (ProducerMonitorData) mergeData;
		all.merge(toMerge.all);
		
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
		
		ProducerData ProducerData = MapUtil.getOrCreate(all, topic, ProducerData.class);
		ProducerData.sendMessage(producerIp, messageId, sendTime, saveTime);
		
		all.getTotal().sendMessage(producerIp, messageId, sendTime, saveTime);
		
	}

	public static class ProducerData extends TotalConcurrentHashMap<String, MessageInfo>{
		
		private static final long serialVersionUID = 1L;
				
		public void sendMessage(String producerIp, long messageId, long sendTime, long saveTime){
			
			MessageInfo messageInfo = MapUtil.getOrCreate(this, producerIp, MessageInfo.class);
			messageInfo.addMessage(messageId, sendTime, saveTime);
			
			total.addMessage(messageId, sendTime, saveTime);
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if(!(obj instanceof ProducerData)){
				return false;
			}
			ProducerData cmp = (ProducerData) obj;
			return cmp.equals(this);
		}

		@Override
		public int hashCode() {
			return hashCode();
		}

		@Override
		protected MessageInfo createValue() {
			return new MessageInfo();
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
