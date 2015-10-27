package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午4:14:34
 */
public class ProducerServerStatisData extends AbstractTotalMapStatisable<ProducerTopicData, ProducerServerData>{

	@Override
	protected Class<? extends Statisable<ProducerTopicData>> getStatisClass() {
		
		return ProducerTopicStatisData.class;
	}

//	@Override
//	public void merge(Mergeable merge){
//
//		AbstractTotalMapStatisable<ProducerTopicData,ProducerServerData> toMerge = (AbstractTotalMapStatisable<ProducerTopicData, ProducerServerData>) merge;
//
//		for(java.util.Map.Entry<String, Statisable<ProducerTopicData>> entry : toMerge.map.entrySet()){
//
//			String key = entry.getKey();
//			Mergeable value = entry.getValue();
//
//			Statisable<ProducerTopicData> myValue = map.get(key);
//			if(myValue == null){
//				myValue= createValue();
//				map.put(key, myValue);
//			}
//			myValue.merge(value);
//		}
//	}

	@Override
	protected Statisable<ProducerTopicData> createValue() {
		return new ProducerTopicStatisData();
	}

}
