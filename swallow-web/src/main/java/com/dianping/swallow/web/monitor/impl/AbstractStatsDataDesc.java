package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:17:08
 */
public abstract class AbstractStatsDataDesc implements StatsDataDesc{

	private String topic;
	
	private StatisDetailType dt = StatisDetailType.SEND_DELAY;
	
	public AbstractStatsDataDesc(String topic, StatisDetailType dt){
		this.setTopic(topic);
		this.dt = dt;
	}

	@Override
	public String getDesc() {
		
		return dt.toString();
	}

	public String getTopic() {
		
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}


}
