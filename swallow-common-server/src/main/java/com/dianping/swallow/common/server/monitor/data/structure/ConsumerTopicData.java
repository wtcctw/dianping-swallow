package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年4月26日 上午9:45:01
 */
public class ConsumerTopicData extends TotalMap<ConsumerIdData>{
	
	private static final long serialVersionUID = 1L;

	public void sendMessage(String consumerId, String consumerIp, SwallowMessage message){
		
		ConsumerIdData consumerIdData = getConsumerIdData(consumerId);
		consumerIdData.sendMessage(consumerIp, message);
	}

	public void removeConsumer(ConsumerInfo consumerInfo) {
		
		remove(consumerInfo.getConsumerId());
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

