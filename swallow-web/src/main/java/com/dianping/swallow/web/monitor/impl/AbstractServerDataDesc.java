package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;


/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:17:08
 */
public abstract class AbstractServerDataDesc extends AbstractStatsDataDesc{
	
	private String serverIp;

	public AbstractServerDataDesc(String serverIp, String topic, StatisDetailType dt) {
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
