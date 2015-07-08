package com.dianping.swallow.web.model.statis.backup;

import java.util.List;

public class ProducerClientStatisData {
	
	private ProducerBaseStatisData statisData;
	
	private List<ProducerClientMachineStatisData> machineStatisDatas;

	public ProducerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ProducerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public List<ProducerClientMachineStatisData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ProducerClientMachineStatisData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
