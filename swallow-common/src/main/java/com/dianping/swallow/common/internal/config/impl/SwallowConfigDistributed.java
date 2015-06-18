package com.dianping.swallow.common.internal.config.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午6:30:53
 */
public class SwallowConfigDistributed extends AbstractSwallowConfig{
	
	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg";//swallow.topiccfg.topc1='';
	
	private Map<String, TopicConfig> topicCfgs = new ConcurrentHashMap<String, SwallowConfig.TopicConfig>();
	
	private JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder(); 

	@Override
	public Set<String> getCfgTopics() {
		
		return topicCfgs.keySet();
	}

	@Override
	public TopicConfig getTopicConfig(String topic) {
		
		TopicConfig rawCfg = topicCfgs.get(topic);
		
		if(rawCfg == null){
			rawCfg = new TopicConfig();
		}
		
		TopicConfig retCfg = null;
		try {
			retCfg = (TopicConfig) rawCfg.clone();
		} catch (CloneNotSupportedException e) {
			
		}
		//融合默认配置
		retCfg.merge(topicCfgs.get(TOPICNAME_DEFAULT));
		return retCfg;
	}

	public TopicConfig getRawTopicConfig(String topic){
		
		return topicCfgs.get(topic);
		
	}
	
	@Override
	public boolean isSupported() {
		
		String defaultCfg = dynamicConfig.get(StringUtils.join(".", TOPIC_CFG_PREFIX, TOPICNAME_DEFAULT));
		
		return !StringUtils.isEmpty(defaultCfg);
	}

	@Override
	protected void doLoadConfig() {
		
		Map<String, String> cfgs = dynamicConfig.getProperties(TOPIC_CFG_PREFIX);
		
		if(cfgs == null){
			throw new IllegalArgumentException("[doLoadConfig][cfg null]");
		}
		
		for(Entry<String, String> entry : cfgs.entrySet()){
			
			String topic = entry.getKey();
			String value = entry.getValue();
			
			if(logger.isInfoEnabled()){
				logger.info("[doLoadConfig]" + topic + ":" + value);
			}
			
			TopicConfig topicCfg = getConfigFromString(value);
			topicCfgs.put(getTopicFromLionKey(topic), topicCfg);
		}
		
		if(topicCfgs.get(TOPICNAME_DEFAULT) == null || !topicCfgs.get(TOPICNAME_DEFAULT).valid()){
			throw new IllegalArgumentException("wrong config, no defalut or default is invalid");
		}
		
	}

	@Override
	protected boolean interested(String key) {
		
		return key.startsWith(TOPIC_CFG_PREFIX);
	}

	@Override
	protected SwallowConfigArgs doOnConfigChange(String key, String value) {
		
		String topic = getTopicFromLionKey(key);
		if(topic == null){
			logger.warn("[doOnConfigChange][not topic cfg]" + key + ":" + value);
			return null;
		}
		
		TopicConfig topicConfig = getConfigFromString(value);
		
		TopicConfig oldCfg = topicCfgs.put(topic, topicConfig);
		if(oldCfg != null){
			logger.info("[doOnConfigChange][replace old cfg]" + topicConfig + "," + oldCfg);
		}
		
		return new SwallowConfigArgs(CHANGED_ITEM.TOPIC_MONGO, topic);
	}

	private TopicConfig getConfigFromString(String config) {
		
		return jsonBinder.fromJson(config, TopicConfig.class);
	}

	private String getTopicFromLionKey(String key) {
		
		if(!key.startsWith(TOPIC_CFG_PREFIX)){
			return null;
		}
		return key.substring(TOPIC_CFG_PREFIX.length() + 1);
	}

}
