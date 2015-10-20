package com.dianping.swallow.web.model.stats;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 下午7:22:18
 */
public abstract class AbstractIpGroupStatsData<T extends AbstractIpStatsData> {

	public List<T> getIpStatsDatas() {
		return ipStatsDatas;
	}

	public void setIpStatsDatas(List<T> ipStatsDatas) {
		this.ipStatsDatas = ipStatsDatas;
	}

	private List<T> ipStatsDatas;

	public boolean hasStatsData() {
		if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
			return false;
		}
		for (T ipStatsData : ipStatsDatas) {
			if (ipStatsData.hasStatsData()) {
				return true;
			}
		}
		return false;
	}
}
