package com.dianping.swallow.web.monitor.impl;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:09
 */
@Component
public class DefaultConsumerDataRetriever extends AbstractMonitorDataRetriever implements ConsumerDataRetriever{

	@Override
	public StatsData getSendDelay(String topic, String consumerId,
			int interval, long start, long end) {
		return null;
	}

	@Override
	public StatsData ackSendDelay(String topic, String consumerId,
			int interval, long start, long end) {
		return null;
	}


}
