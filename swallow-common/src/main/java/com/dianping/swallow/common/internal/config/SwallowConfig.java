package com.dianping.swallow.common.internal.config;

import java.util.Set;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.observer.Observable;


/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午4:38:54
 */
public interface SwallowConfig extends Observable, Lifecycle{
	
	public static final String PROJECT="swallow";
	
	public static final String TOPIC_PREFIX=PROJECT + "." + "topiccfg";
	
	public static final String BASIC_LION_CONFIG_URL = "http://lionapi.dp:8080/config2";

	/**
	 * 获取配置过的topics，没有配置过的使用default配置
	 * @return
	 */
	Set<String> getCfgTopics();

	/**
	 * 获取topic对应的配置信息，如果没有，返回默认配置
	 * @param topic
	 * @return
	 */
	TopicConfig getTopicConfig(String topic);

	TopicConfig defaultTopicConfig();

    GroupConfig getGroupConfig(String group);

}
