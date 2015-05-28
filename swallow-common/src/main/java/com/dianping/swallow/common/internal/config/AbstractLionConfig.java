package com.dianping.swallow.common.internal.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.lion.client.ConfigCache;
import com.dianping.swallow.common.internal.util.StringUtils;


/**
 * 如果lion有配置，覆盖本地配置文件配置
 * @author mengwenchao
 * 
 *         2015年4月18日 下午10:04:45
 */
public class AbstractLionConfig extends AbstractConfig{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String SPLIT = ".";
	
	public static final String BASIC_SUFFIX = "swallow";

	private final String fullSuffix;
	
	private ConfigCache cc;
	
	private boolean isUseLion = true;
	

	public AbstractLionConfig(String localFileConfig, String suffix, boolean isUseLion) {
		
		super(localFileConfig);
		
		if(!StringUtils.isEmpty(suffix)){
			fullSuffix = StringUtils.join(SPLIT, BASIC_SUFFIX, suffix);
		}else{
			fullSuffix = BASIC_SUFFIX;
		}
		cc = ConfigCache.getInstance();
		this.isUseLion = isUseLion;

	}
	
	public AbstractLionConfig(String localFileConfig, String suffix) {
		this(localFileConfig, suffix, true);
	}
	
	@Override
	protected String getValue(String key, Properties props) {
		
		if(isUseLion){
			String lionKey = StringUtils.join(SPLIT, fullSuffix, key);
			String value = cc.getProperty(lionKey);
			if(value != null){
				if(logger.isInfoEnabled()){
					logger.info("[getValue][load value from lion]" + lionKey + ":" + value);
				}
				return value; 
			}
		}
		return super.getValue(key, props);
	}
	
}
