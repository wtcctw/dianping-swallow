package com.dianping.swallow.web.model.statis;

import java.util.List;

public class ConsumerServerStatisData extends AbstractServerStatisData{
	
	private ConsumerBaseStatisData statisData;
	
	private List<ConsumerMachineStatisData> machineStatisDatas;

	public ConsumerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public List<ConsumerMachineStatisData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ConsumerMachineStatisData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
