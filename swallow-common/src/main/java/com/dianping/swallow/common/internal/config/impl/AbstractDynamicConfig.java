package com.dianping.swallow.common.internal.config.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;

/**
 * @author mengwenchao
 *
 * 2015年12月30日 下午5:10:03
 */
public abstract class AbstractDynamicConfig implements DynamicConfig{

	protected final Logger logger = LogManager.getLogger(getClass());

	private Map<ConfigChangeListener, Object> listeners = new HashMap<ConfigChangeListener, Object>();

	@Override
	public synchronized void addConfigChangeListener(final ConfigChangeListener listener) {

		if (listeners.get(listener) != null) {
			throw new IllegalArgumentException("[addConfigChangeListener][already add]" + listener);
		}

		
		Object o = doAddConfigChangeListener(listener);
		listeners.put(listener, o);

		
	}

	protected abstract Object doAddConfigChangeListener(ConfigChangeListener listener);

	@Override
	public synchronized void removeConfigChangeListener(ConfigChangeListener listener) {

		Object change = listeners.get(listener);

		doRemoveConfigChangeListener(change);
		
	}

	protected abstract void doRemoveConfigChangeListener(Object change);

}
