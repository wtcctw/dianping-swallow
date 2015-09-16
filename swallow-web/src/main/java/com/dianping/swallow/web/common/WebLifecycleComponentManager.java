package com.dianping.swallow.web.common;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.server.lifecycle.LifecycleComponentManager;

public class WebLifecycleComponentManager extends LifecycleComponentManager {

	@Override
	protected boolean accept(String key, Lifecycle component) {
		
		return true;
	}
	
}
