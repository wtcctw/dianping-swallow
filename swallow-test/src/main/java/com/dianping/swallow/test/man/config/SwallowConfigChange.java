package com.dianping.swallow.test.man.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigCentral;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigDistributed;


/**
 * @author mengwenchao
 *
 * 2015年6月19日 下午3:29:33
 */
public class SwallowConfigChange {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected LionUtil lionUtil = new LionUtilImpl();
	
	protected Map<String, TopicConfig> oldConfigs = new ConcurrentHashMap<String, SwallowConfig.TopicConfig>();
	
	protected boolean putLionConfig = Boolean.parseBoolean(System.getProperty("putLionConfig"));
	
	public static void main(String []argc) throws Exception{
		
		new SwallowConfigChange().start();
		
		
	}

	private void start() throws Exception {
		
		if(logger.isInfoEnabled()){
			logger.info("[putLionConfig]" + putLionConfig);
		}
		
		createNewConfig();
		
		if(putLionConfig){
			checkConfig();
		}
		
		System.exit(0);
	}

	
	private void checkConfig() throws Exception {
		
		SwallowConfigDistributed config = new SwallowConfigDistributed();
		config.initialize();
		
		Map<String, TopicConfig>  newConfigs = new ConcurrentHashMap<String, SwallowConfig.TopicConfig>();
		for(String topic : config.getCfgTopics()){
			newConfigs.put(topic, config.getTopicConfig(topic));
		}
		
		for(String topic : oldConfigs.keySet()){
			TopicConfig oldConfig = oldConfigs.get(topic);
			
			TopicConfig newConfig = newConfigs.get(topic);
			if(!oldConfig.equals(newConfig)){
				logger.error("[old vs new][" + topic + "]" + oldConfig + ":" + newConfig);
			}
		}
		
		for(String topic : newConfigs.keySet()){
			if(oldConfigs.get(topic) == null){
				logger.error("[old not exist]["+topic+"]" + newConfigs.get(topic));
			}
		}
		
	}

	
	private void createNewConfig() throws Exception {
		
		SwallowConfig centural = new SwallowConfigCentral();
		centural.initialize();
		
		Set<String> topics  = centural.getCfgTopics();

		TopicConfig defaultConfig = centural.getTopicConfig(AbstractSwallowConfig.TOPICNAME_DEFAULT);
		
		logger.info("[newconfig][" + AbstractSwallowConfig.TOPICNAME_DEFAULT + "]" + defaultConfig);
		
		putConfig(AbstractSwallowConfig.TOPICNAME_DEFAULT, defaultConfig);
		oldConfigs.put(AbstractSwallowConfig.TOPICNAME_DEFAULT , defaultConfig);
		
		for(String topic : topics){
			
			if(topic.equals(AbstractSwallowConfig.TOPICNAME_DEFAULT)){
				continue;
			}
			TopicConfig config = centural.getTopicConfig(topic);
			TopicConfig rawConfig = (TopicConfig) config.clone();
			
			oldConfigs.put(topic, rawConfig);
			config.sub(defaultConfig);
			
			if(!config.valid()){
				logger.warn("[after sub, config invalid][" + topic + "]" + rawConfig + ":" + config);
				continue;
			}
			
			logger.info("[newconfig][" + topic + "]" + config);
			putConfig(topic, config);
		}
	}

	private void putConfig(String topic, TopicConfig config) {
		
		if(putLionConfig){
			lionUtil.createOrSetConfig(SwallowConfigCentral.TOPIC_PREFIX + "." + topic, config.toJson());
		}

	}
	
}
