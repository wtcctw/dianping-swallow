package com.dianping.swallow.web.model.statis.backup;

public class ConsumerServerMachineStatisData {

	private ConsumerBaseStatisData statisData;

	private String ip;

	public ConsumerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
