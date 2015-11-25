package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午6:13:17
 */
public class ConsumerTopicStatisData extends AbstractTotalMapStatisable<ConsumerIdData, ConsumerTopicData>{

	@Override
	protected Class<? extends Statisable<ConsumerIdData>> getStatisClass() {
		
		return ConsumerIdStatisData.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void merge(Mergeable merge){

		AbstractTotalMapStatisable<ConsumerIdData,ConsumerTopicData> toMerge = (AbstractTotalMapStatisable<ConsumerIdData, ConsumerTopicData>) merge;

		for(java.util.Map.Entry<String, Statisable<ConsumerIdData>> entry : toMerge.map.entrySet()){

			String key = entry.getKey();
			Mergeable value = entry.getValue();

			Statisable<ConsumerIdData> myValue = map.get(key);
			if(myValue == null){
				myValue= createValue();
				map.put(key, myValue);
			}
			myValue.merge(value);
		}
	}

	@Override
	protected Statisable<ConsumerIdData> createValue() {
		return new ConsumerIdStatisData();
	}

}
