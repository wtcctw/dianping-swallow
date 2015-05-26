package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.data.Statisable;
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

}
