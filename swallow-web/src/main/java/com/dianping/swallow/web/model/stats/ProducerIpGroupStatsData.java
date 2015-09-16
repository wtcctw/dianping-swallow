package com.dianping.swallow.web.model.stats;

import java.util.List;

public class ProducerIpGroupStatsData {

	private List<ProducerIpStatsData> producerIpStatsDatas;

	public List<ProducerIpStatsData> getProducerIpStatsDatas() {
		return producerIpStatsDatas;
	}

	public void setProducerIpStatsDatas(List<ProducerIpStatsData> producerIpStatsDatas) {
		this.producerIpStatsDatas = producerIpStatsDatas;
	}

	public boolean hasStatsData() {
		if (producerIpStatsDatas == null || producerIpStatsDatas.size() == 0) {
			return false;
		}
		for (ProducerIpStatsData ipStatsData : producerIpStatsDatas) {
			if (ipStatsData.getQps() != 0L) {
				return true;
			}
		}
		return false;
	}

}
