package com.dianping.swallow.web.model.statis;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerMachineStatsData {
	
	private String ip;
	
	private ConsumerBaseStatsData statisData;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ConsumerBaseStatsData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatsData statisData) {
		this.statisData = statisData;
	}

}