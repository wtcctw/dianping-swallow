package com.dianping.swallow.web.common;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.AbstractConfig;

/**
 * 
 * @author qiyin
 *
 *         2015年9月18日 上午9:52:22
 */
@Component
public class WebComponentConfig extends AbstractConfig {

	private static final String COMPONENT_SWITCH_FILE = "/data/appdatas/swallowweb/swallow-web-switch.properties";

	private boolean isAlarmer = true;
	private boolean isCollector = true;
	private boolean isStorager = true;
	private boolean isDashboard = true;

	private boolean isJobTask = true;

	public WebComponentConfig() {
		super(COMPONENT_SWITCH_FILE);
	}

	@PostConstruct
	public void init() {
		try {
			loadConfig();
		} catch (Exception e) {
			logger.error("loadConfig error.", e);
		}
	}

	public boolean isAlarmer() {
		return isAlarmer;
	}

	public void setAlarmer(boolean isAlarmer) {
		this.isAlarmer = isAlarmer;
	}

	public boolean isCollector() {
		return isCollector;
	}

	public void setCollector(boolean isCollector) {
		this.isCollector = isCollector;
	}

	public boolean isStorager() {
		return isStorager;
	}

	public void setStorager(boolean isStorager) {
		this.isStorager = isStorager;
	}

	public boolean isDashboard() {
		return isDashboard;
	}

	public void setDashboard(boolean isDashboard) {
		this.isDashboard = isDashboard;
	}

	public boolean isJobTask() {
		return isJobTask;
	}

	public void setJobTask(boolean isJobTask) {
		this.isJobTask = isJobTask;
	}

}
