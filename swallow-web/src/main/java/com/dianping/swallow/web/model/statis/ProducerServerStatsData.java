package com.dianping.swallow.web.model.statis;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerServerStatsData extends AbstractServerStatsData {
	
	private List<ProducerMachineStatsData> statisDatas;

	public List<ProducerMachineStatsData> getStatisDatas() {
		return statisDatas;
	}

	public void setStatisDatas(List<ProducerMachineStatsData> statisDatas) {
		this.statisDatas = statisDatas;
	}

}
