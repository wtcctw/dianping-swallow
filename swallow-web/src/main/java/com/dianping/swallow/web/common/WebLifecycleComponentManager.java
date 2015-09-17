package com.dianping.swallow.web.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.server.lifecycle.LifecycleComponentManager;
import com.dianping.swallow.web.alarmer.AlarmerLifeCycle;
import com.dianping.swallow.web.alarmer.storager.StoragerLifeCycle;
import com.dianping.swallow.web.dashboard.DashboardLifeCycle;
import com.dianping.swallow.web.monitor.collector.CollectorLifeCycle;

public class WebLifecycleComponentManager extends LifecycleComponentManager {

	private static final String COMPONENT_SWITCH = "/data/appdatas/swallowweb/swallow-web-switch.properties";

	private static final String ALARMER_SWITCH = "alarmer";
	private static final String COLLECTOR_SWITCH = "collector";
	private static final String STORAGER_SWITCH = "storager";
	private static final String DASHBOARD_SWITCH = "dashboard";

	private boolean isAlarmer = true;
	private boolean isCollector = true;
	private boolean isStorager = true;
	private boolean isDashboard = true;

	public WebLifecycleComponentManager() {
		initProperties();
	}

	@Override
	protected boolean accept(String key, Lifecycle component) {
		if (component instanceof AlarmerLifeCycle) {
			return isAlarmer;
		} else if (component instanceof CollectorLifeCycle) {
			return isCollector;
		} else if (component instanceof StoragerLifeCycle) {
			return isStorager;
		} else if (component instanceof DashboardLifeCycle) {
			return isDashboard;
		}
		return true;
	}

	private void initProperties() {
		try {
			File file = new File(COMPONENT_SWITCH);
			InputStream in = new FileInputStream(file);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					setAlarmer(Boolean.valueOf(StringUtils.trim(prop.getProperty(ALARMER_SWITCH))));
					setCollector(Boolean.valueOf(StringUtils.trim(prop.getProperty(COLLECTOR_SWITCH))));
					setStorager(Boolean.valueOf(StringUtils.trim(prop.getProperty(STORAGER_SWITCH))));
					setDashboard(Boolean.valueOf(StringUtils.trim(prop.getProperty(DASHBOARD_SWITCH))));
					if (logger.isInfoEnabled()) {
						logger.info("component switch alarmer: " + isAlarmer() + " collector: " + isCollector()
								+ " storager: " + isStorager() + " dashboard: " + isDashboard());
					}

				} finally {
					in.close();
				}
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", COMPONENT_SWITCH);
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

}
