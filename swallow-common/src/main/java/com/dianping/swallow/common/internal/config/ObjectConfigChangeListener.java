package com.dianping.swallow.common.internal.config;

/**
 * @author mengwenchao
 *
 * 2015年6月4日 上午11:44:21
 */
public interface ObjectConfigChangeListener {

	void onChange(Object config, String key) throws Exception;
}
