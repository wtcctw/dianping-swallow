package com.dianping.swallow.web.model.statis;

import java.util.List;

public class ConsumerServerStatsData extends AbstractServerStatsData{
	
	private List<ConsumerMachineStatsData> machineStatisDatas;

	public List<ConsumerMachineStatsData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ConsumerMachineStatsData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

	@Override
	public String toString() {
		return "ConsumerServerStatsData [machineStatisDatas=" + machineStatisDatas + "]";
	}

}
