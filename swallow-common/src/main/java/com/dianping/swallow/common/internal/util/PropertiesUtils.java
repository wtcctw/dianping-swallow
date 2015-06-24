package com.dianping.swallow.common.internal.util;

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午3:10:36
 */
public class PropertiesUtils {

	public static String getProperty(String key, String defaultValue){
		
		String keyValue = System.getProperty(key);
		
		if(StringUtils.isEmpty(keyValue)){
			return defaultValue;
		}
		
		return keyValue.trim();
	}
	
	public static Long getLongProperty(String key, Long defaultValue){
		
		
		String keyValue = System.getProperty(key);
		
		if(StringUtils.isEmpty(keyValue)){
			return defaultValue;
		}
		
		return Long.parseLong(keyValue.trim());
	}
}
