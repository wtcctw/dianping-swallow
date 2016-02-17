package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservableLifecycle;
import com.dianping.swallow.common.internal.util.PropertiesUtils;

/**
 * @author mengwenchao
 *
 * 2016年1月29日 下午5:36:54
 */
public abstract class AbstractSwallowConfig extends AbstractObservableLifecycle implements SwallowConfig, ConfigChangeListener{
	
	protected final String 	LION_CONFIG_FILENAME          = PropertiesUtils.getProperty("SWALLOW.STORE.LION.CONFFILE", "swallow-store-lion.properties");
	
	public static final String 	TOPICNAME_DEFAULT             = "default";
	
	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg";//swallow.topiccfg.topc1='';

    public static final String GROUP_CFG_PREFIX = "swallow.groupcfg";


	protected DynamicConfig                 dynamicConfig;

	public AbstractSwallowConfig(){
		
		dynamicConfig = new DefaultDynamicConfig(LION_CONFIG_FILENAME);
		dynamicConfig.addConfigChangeListener(this);

	}
	
}
