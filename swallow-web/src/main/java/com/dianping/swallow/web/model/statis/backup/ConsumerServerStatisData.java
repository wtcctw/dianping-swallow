package com.dianping.swallow.web.model.statis.backup;

import java.util.List;

public class ConsumerServerStatisData extends AbstractServerStatisData{
	
	private ConsumerBaseStatisData statisData;
	
	private List<ConsumerClientMachineStatisData> machineStatisDatas;

	public ConsumerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public List<ConsumerClientMachineStatisData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ConsumerClientMachineStatisData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
