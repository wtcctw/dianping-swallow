package com.dianping.swallow.web.monitor.impl;


import com.dianping.swallow.web.monitor.StatsDataType;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:17:08
 */
public abstract class AbstractServerDataDesc extends AbstractStatsDataDesc{
	
	private String serverIp;

	public AbstractServerDataDesc(String serverIp, String topic, StatsDataType dt) {
		super(topic, dt);
		this.setServerIp(serverIp);
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

}
