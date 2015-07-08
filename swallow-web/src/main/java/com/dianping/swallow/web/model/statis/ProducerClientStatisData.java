package com.dianping.swallow.web.model.statis;

import java.util.List;

public class ProducerClientStatisData {
	
	private ProducerBaseStatisData statisData;
	
	private List<ProducerMachineStatisData> machineStatisDatas;

	public ProducerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ProducerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public List<ProducerMachineStatisData> getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(List<ProducerMachineStatisData> machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
