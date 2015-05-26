package com.dianping.swallow.web.monitor.impl;


import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:03:56
 */
public class ProducerStatsDataDesc extends AbstractStatsDataDesc implements StatsDataDesc{

	public ProducerStatsDataDesc(String topic) {
		
		super(topic, StatisDetailType.SAVE_DELAY);
	}

	public ProducerStatsDataDesc(String topic, StatisDetailType dt) {
		super(topic, dt);
	}

	
}
