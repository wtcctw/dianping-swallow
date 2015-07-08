package com.dianping.swallow.web.model.statis.backup;

public class ProducerServerMachineStatisData {
	
	private ProducerBaseStatisData statisData;
	
	private String ip;

	public ProducerBaseStatisData getStatisData() {
		return statisData;
	}

	public void setStatisData(ProducerBaseStatisData statisData) {
		this.statisData = statisData;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
