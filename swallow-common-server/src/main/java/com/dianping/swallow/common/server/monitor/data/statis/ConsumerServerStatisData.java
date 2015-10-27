package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午4:14:34
 */
public class ConsumerServerStatisData extends AbstractTotalMapStatisable<ConsumerTopicData, ConsumerServerData>{

	public ConsumerIdStatisData getConsumerId(String topic, String consumerId){
		
		ConsumerTopicStatisData topicStatisData = (ConsumerTopicStatisData) getValue(topic);
		
		ConsumerIdStatisData consumerIdStatisData = (ConsumerIdStatisData) topicStatisData.getValue(consumerId);
		
		
		return consumerIdStatisData;
	}

	@Override
	protected Class<? extends Statisable<ConsumerTopicData>> getStatisClass() {
		
		return ConsumerTopicStatisData.class;
	}

//	@Override
//	public void merge(Mergeable merge){
//
//		AbstractTotalMapStatisable<ConsumerTopicData,ConsumerServerData> toMerge = (AbstractTotalMapStatisable<ConsumerTopicData, ConsumerServerData>) merge;
//
//		for(java.util.Map.Entry<String, Statisable<ConsumerTopicData>> entry : toMerge.map.entrySet()){
//
//			String key = entry.getKey();
//			Mergeable value = entry.getValue();
//
//			Statisable<ConsumerTopicData> myValue = map.get(key);
//			if(myValue == null){
//				myValue= createValue();
//				map.put(key, myValue);
//			}
//			myValue.merge(value);
//		}
//	}

	@Override
	protected Statisable<ConsumerTopicData> createValue() {
		return new ConsumerTopicStatisData();
	}

}
