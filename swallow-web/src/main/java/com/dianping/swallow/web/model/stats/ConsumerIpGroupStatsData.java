package com.dianping.swallow.web.model.stats;

import java.util.List;

public class ConsumerIpGroupStatsData {

	private List<ConsumerIpStatsData> consumerIpStatsDatas;

	public List<ConsumerIpStatsData> getConsumerIpStatsDatas() {
		return consumerIpStatsDatas;
	}

	public void setConsumerIpStatsDatas(List<ConsumerIpStatsData> consumerIpStatsDatas) {
		this.consumerIpStatsDatas = consumerIpStatsDatas;
	}

	public boolean hasStatsData() {
		if (consumerIpStatsDatas == null || consumerIpStatsDatas.size() == 0) {
			return false;
		}
		for (ConsumerIpStatsData ipStatsData : consumerIpStatsDatas) {
			if (ipStatsData.getSendQps() != 0L || ipStatsData.getAckQps() != 0L) {
				return true;
			}
		}
		return false;
	}
	
}
