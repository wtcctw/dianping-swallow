package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.server.monitor.data.ProducerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:46:41
 */
public class ProducerAllData extends AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> 
						implements ProducerStatisRetriever{
	
	public ProducerAllData(){
			super(StatisType.SAVE);
	}

	@Override
	protected Class<? extends ProducerServerStatisData> getStatisClass() {
		
		return ProducerServerStatisData.class;
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		ProducerAllData clone = (ProducerAllData) super.clone();
		return clone;
	}

	@Override
	public ProducerServerStatisData createValue() {
		return new ProducerServerStatisData();
	}

}
