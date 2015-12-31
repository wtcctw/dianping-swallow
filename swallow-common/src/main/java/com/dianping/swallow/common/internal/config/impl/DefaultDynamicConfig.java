package com.dianping.swallow.common.internal.config.impl;

import java.util.Map;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.file.FileDynamicConfig;
import com.dianping.swallow.common.internal.config.impl.lion.LionDynamicConfig;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.PropertiesUtils;

/**
 * 在dev环境，优先使用本地配置
 * 
 * @author mengwenchao
 * 
 *         2015年5月28日 上午10:30:15
 */
public class DefaultDynamicConfig implements DynamicConfig {
	
	private final boolean useLocal = Boolean.parseBoolean(PropertiesUtils.getProperty("lion.useLocal", "false"));
	
	private DynamicConfig  dynamicConfig;
	
	public DefaultDynamicConfig(){
		this(null);
	}
	
	public DefaultDynamicConfig(String localConfigFileName){
		
		if(useLocal()){
			dynamicConfig = new FileDynamicConfig(localConfigFileName);
		}else{
			dynamicConfig = new LionDynamicConfig();
		}
	}

	private boolean useLocal() {
		
		return EnvUtil.isDev() || useLocal;
	}

	@Override
	public String get(String key) {
		
		return dynamicConfig.get(key);
	}

	@Override
	public Map<String, String> getProperties(String prefix) {
		
		return dynamicConfig.getProperties(prefix);
	}

	@Override
	public void addConfigChangeListener(ConfigChangeListener listener) {
		
		dynamicConfig.addConfigChangeListener(listener);
		
	}

	@Override
	public void removeConfigChangeListener(ConfigChangeListener listener) {
		dynamicConfig.removeConfigChangeListener(listener);
	}

}
