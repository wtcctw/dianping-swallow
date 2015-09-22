package com.dianping.swallow.common.internal.config;

import java.util.Map;

/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午4:01:17
 */
public interface LionUtil {
	
	void createOrSetConfig(String key, String value);

	void createOrSetConfig(String key, String value, HttpMethod method);
	
	Map<String, String> getCfgs(String prefix);
	
}