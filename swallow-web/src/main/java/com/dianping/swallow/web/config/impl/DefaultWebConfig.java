package com.dianping.swallow.web.config.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.AbstractLionConfig;
import com.dianping.swallow.web.config.WebConfig;

/**
 * @author mengwenchao
 *
 * 2015年6月4日 下午1:13:36
 */
@Component
public class DefaultWebConfig extends AbstractLionConfig implements WebConfig{
	
	private static final String CONFIG_FILE = "swallow-web-lion.properties";
	
	private static final String WEB_CONFIG_SUFFIX = "webconfig";//swallow.webconfig.(xxx)
	
	private int accumulationBuildInterval = 60;//seconds
	
	public static final String FIELD_ACCUMULATION = "accumulationBuildInterval";
	
	public DefaultWebConfig() {
		
		super(CONFIG_FILE, WEB_CONFIG_SUFFIX);
	}
	
	@PostConstruct
	public void postDefaultWebConfig(){
		loadConfig();
	}
	

	public int getAccumulationBuildInterval() {
		return accumulationBuildInterval;
	}

}
