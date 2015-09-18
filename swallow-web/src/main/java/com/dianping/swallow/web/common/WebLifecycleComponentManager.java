package com.dianping.swallow.web.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.server.lifecycle.LifecycleComponentManager;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.storager.StoragerLifecycle;
import com.dianping.swallow.web.dashboard.DashboardLifecycle;
import com.dianping.swallow.web.monitor.collector.CollectorLifecycle;

/**
 * 
 * @author qiyin
 *
 *         2015年9月18日 上午9:52:08
 */
public class WebLifecycleComponentManager extends LifecycleComponentManager {

	@Autowired
	private WebComponentConfig componentConfig;

	public WebLifecycleComponentManager() {
		
	}

	@Override
	protected boolean accept(String key, Lifecycle component) {
		if (component instanceof AlarmerLifecycle) {
			return componentConfig.isAlarmer();
		} else if (component instanceof CollectorLifecycle) {
			return componentConfig.isCollector();
		} else if (component instanceof StoragerLifecycle) {
			return componentConfig.isStorager();
		} else if (component instanceof DashboardLifecycle) {
			return componentConfig.isDashboard();
		}
		return true;
	}

}
