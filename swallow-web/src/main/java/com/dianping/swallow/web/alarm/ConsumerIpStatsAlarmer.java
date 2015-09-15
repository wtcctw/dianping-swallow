package com.dianping.swallow.web.alarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;

public class ConsumerIpStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	private ConsumerStatsDataWapper statsDataWapper;

	@Override
	public void doAlarm() {

		List<ConsumerIpStatsData> ipStatsDatas = statsDataWapper.getConsumerIpStatsDatas(getLastTimeKey());
		for (final ConsumerIpStatsData ipStatsData : ipStatsDatas) {
			executor.submit(new Runnable() {

				@Override
				public void run() {
					ipAlarm(ipStatsData);
				}

			});
		}
	}

	public boolean ipAlarm(ConsumerIpStatsData ipStatsData) {
		return true;
	}

}
