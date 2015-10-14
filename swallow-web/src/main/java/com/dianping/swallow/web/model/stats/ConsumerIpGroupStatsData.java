package com.dianping.swallow.web.model.stats;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午1:16:01
 */
public class ConsumerIpGroupStatsData {

	private List<ConsumerIpStatsData> consumerIpStatsDatas;

	public List<ConsumerIpStatsData> getConsumerIpStatsDatas() {
		return consumerIpStatsDatas;
	}

	public void setConsumerIpStatsDatas(List<ConsumerIpStatsData> consumerIpStatsDatas) {
		this.consumerIpStatsDatas = consumerIpStatsDatas;
	}

	public boolean hasStatsData() {
		if (consumerIpStatsDatas == null || consumerIpStatsDatas.isEmpty()) {
			return false;
		}
		for (ConsumerIpStatsData ipStatsData : consumerIpStatsDatas) {
			if (ipStatsData.hasStatsData()) {
				return true;
			}
		}
		return false;
	}

}
