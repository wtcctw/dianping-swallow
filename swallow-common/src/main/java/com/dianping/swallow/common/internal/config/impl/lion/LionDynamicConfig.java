package com.dianping.swallow.common.internal.config.impl.lion;

import java.util.Map;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.impl.AbstractDynamicConfig;

/**
 * @author mengwenchao
 *
 * 2015年12月30日 下午5:09:12
 */
public class LionDynamicConfig extends AbstractDynamicConfig{


	private ConfigCache cc = ConfigCache.getInstance();

	public LionDynamicConfig() {
		
	}

	@Override
	public String get(String key) {
		try {
			return cc.getProperty(key);
		} catch (LionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> getProperties(String prefix) {

		LionUtil lionUtil = new LionUtilImpl();
		return lionUtil.getCfgs(prefix);
	}

	@Override
	protected Object doAddConfigChangeListener(final ConfigChangeListener listener) {
		
		ConfigChange configChange = new ConfigChange() {

			@Override
			public void onChange(String key, String value) {
				try{
					listener.onConfigChange(key, value);
				}catch(Exception e){
					logger.error("[onChange]" + key + "," + value, e);
				}
			}
		};
		cc.addChange(configChange);
		
		return configChange;
	}

	@Override
	protected void doRemoveConfigChangeListener(Object change) {
		
		if (change != null) {
			cc.removeChange((ConfigChange) change);
		}
	}

}
