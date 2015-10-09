package com.dianping.swallow.web.model.stats;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 *         2015年9月18日 下午6:05:42
 */
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
