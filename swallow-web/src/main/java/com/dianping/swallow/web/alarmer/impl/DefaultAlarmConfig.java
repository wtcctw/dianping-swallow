package com.dianping.swallow.web.alarmer.impl;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.AbstractConfig;
import com.dianping.swallow.web.alarmer.AlarmConfig;

/**
 * 
 * @author qiyin
 *
 *         2015年9月21日 下午2:40:54
 */
@Component
public class DefaultAlarmConfig extends AbstractConfig implements AlarmConfig {

	protected static String SERVER_CHECK_URL_FILE = "server-check-url.properties";

	private String pigeonHealthUrl;

	private String slaveMonitorUrl;

	private String serverMonitorUrl;

	public DefaultAlarmConfig() {
		super(SERVER_CHECK_URL_FILE);
	}

	public String getSlaveMonitorUrl() {
		return slaveMonitorUrl;
	}

	public void setSlaveMonitorUrl(String slaveMonitorUrl) {
		this.slaveMonitorUrl = slaveMonitorUrl;
	}

	public String getServerMonitorUrl() {
		return serverMonitorUrl;
	}

	public void setServerMonitorUrl(String serverMonitorUrl) {
		this.serverMonitorUrl = serverMonitorUrl;
	}

	public String getPigeonHealthUrl() {
		return pigeonHealthUrl;
	}

	public void setPigeonHealthUrl(String pigeonHealthUrl) {
		this.pigeonHealthUrl = pigeonHealthUrl;
	}

}
