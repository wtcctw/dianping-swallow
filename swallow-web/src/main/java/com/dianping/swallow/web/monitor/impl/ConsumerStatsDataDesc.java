package com.dianping.swallow.web.monitor.impl;


import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:03:56
 */
public class ConsumerStatsDataDesc extends AbstractStatsDataDesc implements StatsDataDesc{
	
	private String consumerId;
	
	public ConsumerStatsDataDesc(String topic, StatisDetailType dt){
		super(topic, dt);
	}

	public ConsumerStatsDataDesc(String topic, String consumerId, StatisDetailType dt){
		super(topic, dt);
		this.setConsumerId(consumerId);
	}

	public String getConsumerId() {
		
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

}
