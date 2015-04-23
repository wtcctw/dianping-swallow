package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.monitor.StatsDataType;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:03:56
 */
public class ProducerStatsDataDesc extends AbstractStatsDataDesc implements StatsDataDesc{

	public ProducerStatsDataDesc(String topic) {
		super(topic, StatsDataType.SAVE_DELAY);
	}

	public ProducerStatsDataDesc(String topic, StatsDataType dt) {
		super(topic, dt);
	}

	
}
