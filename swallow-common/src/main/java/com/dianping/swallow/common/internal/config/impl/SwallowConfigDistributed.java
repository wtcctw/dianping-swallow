package com.dianping.swallow.common.internal.config.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.exception.SwallowAlertException;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午6:30:53
 */
public class SwallowConfigDistributed extends AbstractSwallowConfig implements Runnable{
	
	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg";//swallow.topiccfg.topc1='';
	
	private Map<String, TopicConfig> topicCfgs = new ConcurrentHashMap<String, TopicConfig>();
	
	private JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
	
	private Thread checkNewConfig;
	
	protected final static int CHECK_NEW_CONFIG_INTERVAL = 5;//SECONDS
	
	protected final static int CHECK_NEW_CONFIG_MAX = 20;//一次检查，如果多于N个增加或者删除，废弃(可能lion系统故障)

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
		
		for(String topicKey : cfgs.keySet()){
			
			putConfig(topicKey);
		}
		
		if(topicCfgs.get(TOPICNAME_DEFAULT) == null || !topicCfgs.get(TOPICNAME_DEFAULT).valid()){
			throw new IllegalArgumentException("wrong config, no defalut or default is invalid");
		}
		
		
		checkNewConfig = new Thread(this);
		checkNewConfig.setName("SwallowConfigDistributed-checkNewConfig");
		checkNewConfig.setDaemon(true);
		checkNewConfig.start();
	}

	private TopicConfig putConfig(String topicKey) {
		
		String value = dynamicConfig.get(getLionKeyFromTopic(topicKey));//从lion获取，在lion更新的时候通知
		
		if(logger.isInfoEnabled()){
			logger.info("[putConfig]" + topicKey + ":" + value);
		}
		
		TopicConfig topicCfg = getConfigFromString(value);
		
		putConfig(topicKey, topicCfg);
		
		return topicCfg;
		
	}

	/**
	 * 放置新配置，返回老配置
	 * 如果新配置为null，清除老配置
	 * @param topicKey
	 * @param topicCfg
	 */
	public TopicConfig putConfig(String topicKey, TopicConfig topicCfg) {
		
		String topic = getTopicFromLionKey(topicKey);
		TopicConfig oldConfig = null;
		
		if(topicCfg == null){
			if(logger.isInfoEnabled()){
				logger.info("[putConfig][config null]" +topicKey + "," + topicCfg);
			}
			topicCfg = new TopicConfig();
		}
		
		oldConfig = topicCfgs.put(topic, topicCfg);
		if(oldConfig != null){
			logger.info("[putConfig][replace old config]" + oldConfig);
		}
		return oldConfig;
	}

	@Override
	protected boolean interested(String key) {
		
		return key.startsWith(TOPIC_CFG_PREFIX);
	}
	
	public static String topicKey(String topic){
		
		if(topic.startsWith(TOPIC_CFG_PREFIX)){
			return topic;
		}
		
		return StringUtils.join(",", TOPIC_CFG_PREFIX, topic);
	}

	@Override
	protected SwallowConfigArgs doOnConfigChange(String key, String value) {
		
		String topic = getTopicFromLionKey(key);
		if(topic == null){
			logger.warn("[doOnConfigChange][not topic cfg]" + key + ":" + value);
			return null;
		}
		
		TopicConfig topicConfig = getConfigFromString(value);
		
		TopicConfig oldConfig = putConfig(topic, topicConfig);
		
		if((oldConfig == topicConfig) || (oldConfig!= null && oldConfig.equals(topicConfig))){
			logger.info("[doOnConfigChange][old == new]");
			return null;
		}
		
		return new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, topic, oldConfig);
	}

	private TopicConfig getConfigFromString(String config) {
		try{
			return jsonBinder.fromJson(config, TopicConfig.class);
		}catch(Exception e){
			logger.error("[getConfigFromString][config]" + config, e);
			return null;
		}
	}

	private String getTopicFromLionKey(String key) {
		
		if(!key.startsWith(TOPIC_CFG_PREFIX)){
			
			if(key.indexOf(".") >= 0){//有切分符号，但是非topic配置
				return null;
			}
			return key;
		}
		return key.substring(TOPIC_CFG_PREFIX.length() + 1);
	}


	private String getLionKeyFromTopic(String topic){
		
		if(topic.startsWith(TOPIC_CFG_PREFIX)){
			return topic;
		}
		
		return TOPIC_CFG_PREFIX + "." + topic;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {

		while(!Thread.currentThread().interrupted()){
			
			try{
	
				TimeUnit.SECONDS.sleep(CHECK_NEW_CONFIG_INTERVAL);
				
				Map<String, String> cfgs = dynamicConfig.getProperties(TOPIC_CFG_PREFIX);
				Set<String>  rawTopicSet = topicCfgs.keySet();
				Set<String>	 currentTopicSet = new HashSet<String>();
				
				for(String topicKey :  cfgs.keySet()){
					String topic = getTopicFromLionKey(topicKey);
					if(topic == null){
						continue;
					}
					currentTopicSet.add(topic);
				}
				
				if(logger.isDebugEnabled()){
					logger.debug("[run][rawTopicSet]" + rawTopicSet);
					logger.debug("[run][currentTopicSet]" + currentTopicSet);
				}
				
				if(!currentTopicSet.contains(TOPICNAME_DEFAULT)){
					logger.error("[run][no default in currentTopicSet]");
					return;
				}
				
				checkAdd(new HashSet<String>(rawTopicSet), new HashSet<String>(currentTopicSet));
				checkDelete(new HashSet<String>(rawTopicSet), new HashSet<String>(currentTopicSet));
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
				logger.error("[run]", e);
			}catch(Throwable th){
				logger.error("[run]", th);
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[run][exit]");
		}
	}

	private void checkDelete(Set<String> rawTopicSet, Set<String> currentTopicSet) {
		
		rawTopicSet.removeAll(currentTopicSet);
		if(rawTopicSet.size() <= 0){
			return;
		}
		
		if(checkChangeSizeShouldExit(rawTopicSet, "checkDelete")){
			return;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[checkDelete][delete config]" + rawTopicSet);
		}
		
		for(String topic : rawTopicSet){
			
			if(topic.equals(TOPICNAME_DEFAULT)){
				logger.error("[checkDelete][can not delete default config]");
				continue;
			}
			
			TopicConfig cfg = topicCfgs.remove(topic);
			if(cfg == null){
				logger.warn("[checkDelete][config not exist]" + topic);
			}
			updateObservers(new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, getTopicFromLionKey(topic), CHANGED_BEHAVIOR.DELETE));
		}
		
	}

	private void checkAdd(Set<String> rawTopicSet, Set<String> currentTopicSet) {
		
		currentTopicSet.removeAll(rawTopicSet);
		if(currentTopicSet.size() <= 0){
			return;
		}
		
		if(checkChangeSizeShouldExit(currentTopicSet, "checkAdd")){
			return;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[checkAdd][add new config]" + currentTopicSet);
		}

		for(String topicKey : currentTopicSet){
			putConfig(topicKey);
			updateObservers(new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, getTopicFromLionKey(topicKey), CHANGED_BEHAVIOR.ADD));
		}
	}

	private boolean checkChangeSizeShouldExit(Set<String> topics, String func) {
		
		if(topics.size() >= CHECK_NEW_CONFIG_MAX){
			
			logger.error("[" + func + "]", new SwallowAlertException("["+func+"][size]" + topics.size()) + ",[topics]" + topics);
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return topicCfgs.toString();
	}
}
