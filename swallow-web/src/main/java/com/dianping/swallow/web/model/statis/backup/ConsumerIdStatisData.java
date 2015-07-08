package com.dianping.swallow.web.model.statis.backup;

public class ConsumerIdStatisData {
	
	private String consumerId;
	
	private ConsumerBaseStatisData statisData;
	
	private ConsumerClientMachineStatisData machineStatisDatas;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public ConsumerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public ConsumerClientMachineStatisData getMachineStatisDatas() {
		return machineStatisDatas;
	}

	public void setMachineStatisDatas(ConsumerClientMachineStatisData machineStatisDatas) {
		this.machineStatisDatas = machineStatisDatas;
	}

}
