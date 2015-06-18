package com.dianping.swallow.test.man.lion;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigCentral;


/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午5:18:53
 */
public class ChangeLionConfig {
	
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());
	
	public static void main(String []argc) throws Exception{
		
		new ChangeLionConfig().doJob();
	}

	private void doJob() throws Exception {
		
		SwallowConfigCentral centural = new SwallowConfigCentral();
		centural.initialize();

		Map<String, TopicConfig> cfgs = new HashMap<String, SwallowConfig.TopicConfig>();
		
		TopicConfig defaultConfig = centural.getTopicConfig(AbstractSwallowConfig.TOPICNAME_DEFAULT);
		cfgs.put(AbstractSwallowConfig.TOPICNAME_DEFAULT, defaultConfig);
		
		for(String topic : centural.getCfgTopics()){

			if(topic.equals(AbstractSwallowConfig.TOPICNAME_DEFAULT)){
				continue;
			}
			
			
			TopicConfig topicConfig = centural.getTopicConfig(topic);
			topicConfig.sub(defaultConfig);
			if(topicConfig.valid()){
				cfgs.put(topic, topicConfig);
			}
		}
	
		putNewConfig(cfgs);
	}

	private void putNewConfig(Map<String, TopicConfig> cfgs) {
		
		LionUtil lionUtil = new LionUtilImpl();
		
		for(Entry<String, TopicConfig> entry : cfgs.entrySet()){
			
			String topic =   entry.getKey();
			TopicConfig config = entry.getValue();
			logger.info("[putNewConfig]" + topic + ":" + config );
//			lionUtil.createOrSetConfig(SwallowConfigDistributed.TOPIC_CFG_PREFIX + "." + topic, config.toJson());
		}

	}
}
