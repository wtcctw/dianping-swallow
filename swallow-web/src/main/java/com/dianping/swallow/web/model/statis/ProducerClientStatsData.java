package com.dianping.swallow.web.model.statis;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientStatsData {
	
	private ProducerBaseStatsData statisData;
	
	private List<ProducerMachineStatsData> machineStatisDatas;

	public ProducerBaseStatsData getStatisData() {
		return statisData;
	}

	public void setStatisData(ProducerBaseStatsData statisData) {
		this.statisData = statisData;
	}

	public List<ProducerMachineStatsData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ProducerMachineStatsData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
