package com.dianping.swallow.web.model.statis;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerMachineStatsData {

	private String ip;
	
	private ProducerBaseStatsData statisData;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ProducerBaseStatsData getStatisData() {
		return statisData;
	}

	public void setStatisData(ProducerBaseStatsData statisData) {
		this.statisData = statisData;
	}
}