package com.dianping.swallow.web.model.statis;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerMachineStatsData {

	private String ip;
	
	private long timeKey;
	
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

	public long getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(long timeKey) {
		this.timeKey = timeKey;
	}
}