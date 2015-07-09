package com.dianping.swallow.web.model.statis;

import java.util.List;

public class ConsumerServerStatsData extends AbstractServerStatsData{
	
	private ConsumerBaseStatsData statisData;
	
	private List<ConsumerMachineStatsData> machineStatisDatas;

	public ConsumerBaseStatsData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatsData statisData) {
		this.statisData = statisData;
	}

	public List<ConsumerMachineStatsData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ConsumerMachineStatsData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
