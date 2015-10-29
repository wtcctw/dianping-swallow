package com.dianping.swallow.common.internal.config.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.PropertiesUtils;

/**
 * 在dev环境，优先使用本地配置
 * 
 * @author mengwenchao
 * 
 *         2015年5月28日 上午10:30:15
 */
public class LionDynamicConfig implements DynamicConfig {

	private static final Logger logger = LogManager.getLogger(LionDynamicConfig.class);

	private ConfigCache cc;

	public LionDynamicConfig(String localConfigFileName) {

		try {
			cc = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			boolean useLocal = Boolean.parseBoolean(PropertiesUtils.getProperty("lion.useLocal", "false"));
			if (EnvUtil.isDev() || useLocal) {
				// 如果本地文件存在，则使用Lion本地文件
				InputStream in = LionDynamicConfig.class.getClassLoader().getResourceAsStream(localConfigFileName);
				if (in != null) {
					if (logger.isInfoEnabled()) {
						logger.info("[loading]" + localConfigFileName);
					}
					try {
						Properties props = new Properties();
						props.load(in);
						Properties oldProps = cc.getPts();
						if (oldProps != null) {
							oldProps.putAll(props);
						} else {
							cc.setPts(props);
						}
						if (logger.isInfoEnabled()) {
							logger.info("Load Lion local config file :" + localConfigFileName);
						}
					} finally {
						in.close();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String key) {
		try {
			return cc.getProperty(key);
		} catch (LionException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<ConfigChangeListener, ConfigChange> listeners = new HashMap<ConfigChangeListener, ConfigChange>();

	@Override
	public synchronized void addConfigChangeListener(final ConfigChangeListener listener) {

		if (listeners.get(listener) != null) {
			throw new IllegalArgumentException("[addConfigChangeListener][already add]" + listener);
		}

		ConfigChange configChange = new ConfigChange() {

			@Override
			public void onChange(String key, String value) {
				listener.onConfigChange(key, value);
			}
		};

		listeners.put(listener, configChange);
		cc.addChange(configChange);
	}

	@Override
	public synchronized void removeConfigChangeListener(ConfigChangeListener listener) {

		ConfigChange change = listeners.get(listener);
		if (change != null) {
			cc.removeChange(change);
		}
	}

	@Override
	public Map<String, String> getProperties(String prefix) {

		if (EnvUtil.isDev()) {

			Map<String, String> result = new HashMap<String, String>();
			for (Entry<Object, Object> entry : cc.getPts().entrySet()) {

				String key = (String) entry.getKey();
				String value = (String) entry.getValue();

				if (key.startsWith(prefix)) {
					result.put(key, value);
				}
			}

			return result;
		}

		LionUtil lionUtil = new LionUtilImpl();
		return lionUtil.getCfgs(prefix);
	}
}
