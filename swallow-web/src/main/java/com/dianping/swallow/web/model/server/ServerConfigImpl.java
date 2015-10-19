package com.dianping.swallow.web.model.server;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.AbstractConfig;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:41:44
 */
@Component
public class ServerConfigImpl extends AbstractConfig implements ServerConfig {

	private static final String SERVER_CHECK_URL_FILE = "server-check-url.properties";

	private String pigeonHealthUrl = "http://{ip}:4080/stats.json";

	private String slaveMonitorUrl = "http://{ip}:8080/names";

	public ServerConfigImpl() {
		super(SERVER_CHECK_URL_FILE);
	}

	@PostConstruct
	public void initConfig() {
		loadConfig();
	}

	public String getPigeonHealthUrl() {
		return pigeonHealthUrl;
	}

	public String getSlaveMonitorUrl() {
		return slaveMonitorUrl;
	}

}
