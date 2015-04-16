package com.dianping.swallow.common.internal.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
	

	private Map<String, ConsumerData>  all = new ConcurrentHashMap<String, ConsumerMonitorData.ConsumerData>();
	
	
	public ConsumerMonitorData(){
		
	}
	
	public ConsumerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}
	
	
	public void addSendData(ConsumerInfo consumerInfo, SwallowMessage message){
		
		ConsumerData consumerData = getConsumerData(consumerInfo);
		consumerData.sendMessage(message);
				
	}

	public void addAckData(ConsumerInfo consumerInfo, SwallowMessage message){
		
		ConsumerData consumerData = getConsumerData(consumerInfo);
		consumerData.ackMessage(message);;
	}
	
	
	
	private ConsumerData getConsumerData(ConsumerInfo consumerInfo) {
		
		ConsumerData consumerData;
		
		synchronized (consumerInfo) {
			
			consumerData = all.get(consumerInfo);
			if(consumerData == null){
				consumerData = new ConsumerData();
				all.put(getConsumerDesc(consumerInfo), consumerData);
			}
		}
		return consumerData;
	}

	private String getConsumerDesc(ConsumerInfo consumerInfo) {
		return consumerInfo.getDest().getName() + "," + consumerInfo.getConsumerId();
	}


	public static class ConsumerData{
				
		protected transient final Logger logger = LoggerFactory.getLogger(getClass());

		private AtomicLong sendCount = new AtomicLong();
		private AtomicLong sendTotalDelay = new AtomicLong();
		
		private AtomicLong ackCount = new AtomicLong();
		private AtomicLong ackTotalDelay = new AtomicLong();
		
		
		private Map<Long, Long>  messageSendTimes = new ConcurrentHashMap<Long, Long>();
		
		public void sendMessage(SwallowMessage message){
			
			//记录消息发送时间
			messageSendTimes.put(message.getMessageId(), System.currentTimeMillis());
			
			sendCount.incrementAndGet();
			
			long saveTime = SwallowMessageUtil.getSaveTime(message);
			long delay = 0;
			
			if(saveTime > 0){
				delay = System.currentTimeMillis() - saveTime;
			}
			sendTotalDelay.addAndGet(delay);
		}
		
		public void ackMessage(SwallowMessage message){
			
			Long messageId = message.getMessageId();
			Long sendTime = messageSendTimes.get(messageId);
			
			if(sendTime == null){
				logger.warn("[ackMessage][unfound message]" + messageId);
				sendTime = System.currentTimeMillis();
			}
			try{
				ackCount.incrementAndGet();
				ackTotalDelay.addAndGet(System.currentTimeMillis() - sendTime);
			}finally{
				messageSendTimes.remove(messageId);
			}
		}
	
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ConsumerData)){
				return false;
			}
			ConsumerData cmp = (ConsumerData) obj;
			return cmp.ackCount.get() == ackCount.get()
					&& cmp.ackTotalDelay.get() == ackTotalDelay.get()
					&& cmp.sendCount.get() == sendCount.get()
					&& cmp.sendTotalDelay.get() == sendTotalDelay.get();
		}
		
		@Override
		public int hashCode() {
			
			return (int) (ackCount.get() ^ ackTotalDelay.get() ^ sendCount.get() ^ sendTotalDelay.get());
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
