package com.dianping.swallow.web.common;

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

	private boolean isAlarmer = true;
	private boolean isCollector = true;
	private boolean isStorager = true;
	private boolean isDashboard = true;

	public WebComponentConfig() {

	}

	@Override
	protected void loadConfig() {

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

}
