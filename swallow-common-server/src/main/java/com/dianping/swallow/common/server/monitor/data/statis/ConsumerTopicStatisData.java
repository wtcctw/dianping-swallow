package com.dianping.swallow.common.server.monitor.data.statis;

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

}
