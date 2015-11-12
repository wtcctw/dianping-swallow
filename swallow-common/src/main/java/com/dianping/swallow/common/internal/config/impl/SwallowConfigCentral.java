package com.dianping.swallow.common.internal.config.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 * 
 *         2015年6月10日 下午5:05:39
 */
public class SwallowConfigCentral extends AbstractSwallowConfig implements SwallowConfig {

	protected static final String LION_KEY_MONGO_URLS = "swallow.mongo.producerServerURI";

	protected static final String LION_KEY_MSG_CAPPED_COLLECTION_SIZE = "swallow.mongo.msgCappedCollectionSize";

	protected static final String LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM = "swallow.mongo.msgCappedCollectionMaxDocNum";

	private Map<String, String> 	topicNameToMongo;
	
	private Map<String, Integer> 	topicNameToSizes;

	private Map<String, Integer> 	topicNameToMaxDocNums;
	
	private final Set<String>		interestedKeys = new HashSet<String>();

	public SwallowConfigCentral() throws Exception {
		
		interestedKeys.add(LION_KEY_MONGO_URLS);
		interestedKeys.add(LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
		interestedKeys.add(LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
		
	}

	@Override
	protected void doLoadConfig() {

		String topicNameToMongoCfg = dynamicConfig.get(LION_KEY_MONGO_URLS);
		
		if(!StringUtils.isEmpty(topicNameToMongoCfg)){
			topicNameToMongo = parseServerURIString(topicNameToMongoCfg.trim(), LION_KEY_MONGO_URLS);
		}
		
		String msgTopicNameToSizesCfg = dynamicConfig.get(LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
		if(!StringUtils.isEmpty(msgTopicNameToSizesCfg)){
			topicNameToSizes = parseSizeOrDocNum(msgTopicNameToSizesCfg.trim(), LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
		}

		String msgTopicNameToMaxDocNumsCfg = dynamicConfig.get(LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
		
		if(!StringUtils.isEmpty(msgTopicNameToMaxDocNumsCfg)){
			topicNameToMaxDocNums = parseSizeOrDocNum(msgTopicNameToMaxDocNumsCfg.trim(), LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
		}
	}

	
	protected Map<String, String> parseServerURIString(String serverURI, String configKey) {
		
	    Map<String, String> result = new HashMap<String, String>();
	    
	    for (String topicNamesToURI : serverURI.split("\\s*;\\s*")) {
	    	
	    	if(StringUtils.isEmpty(topicNamesToURI)){
	    		continue;
	    	}
	    	
	    	String[] splits = topicNamesToURI.split("=");
	    	if(splits.length != 2){
	    		logger.error("[parseServerURIString][wrong config]" + topicNamesToURI);
	    		continue;
	    	}
	    	String mongoURI = splits[1].trim();
	    	String topicNameStr = splits[0].trim();
	       
	    	for (String topicName : topicNameStr.split(",")) {
	    		topicName = topicName.trim();
	    		if(StringUtils.isEmpty(topicName)){
	    			continue;
	    		}
	    		String previous = result.put(topicName, mongoURI);
	    		if(previous != null){
	    			logger.error("[parseServerURIString][multi topic config][" + topicName+ "]" + previous + "," + mongoURI);
	    		}
	    	}
	    }
	    
	    if(logger.isInfoEnabled()){
	    	logger.info("[parseServerURIString][parse]" + serverURI);
	    }
	    if (result.get(TOPICNAME_DEFAULT) == null) {
	    	//throw new IllegalArgumentException("The config  " + configKey + "  property must contain 'default' topicName!");
	    }
	    return result;
	}

	private Map<String, Integer> parseSizeOrDocNum(String sizeStr, String configKey) {
		try {
			Map<String, Integer> topicNameToSizes = new HashMap<String, Integer>();
			boolean defaultExists = false;
			for (String topicNameToSize : sizeStr.split("\\s*;\\s*")) {
				
				if(StringUtils.isEmpty(topicNameToSize)){
					continue;
				}
				
				String[] splits = topicNameToSize.split("=");
		    	if(splits.length != 2){
		    		logger.error("[parseSizeOrDocNum][wrong config]" + topicNameToSize);
		    		continue;
		    	}
				String size = splits[1];
				String topicNameStr = splits[0];
				
				for (String topicName : topicNameStr.split(",")) {
					if (TOPICNAME_DEFAULT.equals(topicName)) {
						defaultExists = true;
					}
					int intSize = Integer.parseInt(size);
					if (intSize <= 0) {
						throw new IllegalArgumentException(
								"Size or DocNum value must larger than 0 :"
										+ sizeStr);
					}
					topicNameToSizes.put(topicName, intSize);
				}
			}

			if (!defaultExists) {
				throw new IllegalArgumentException("The '" + configKey + "' property must contain 'default' topicName!");
			}
			if (logger.isInfoEnabled()) {
				logger.info("parseSizeOrDocNum() - parse " + sizeStr + " to: " + topicNameToSizes);
			}
			return topicNameToSizes;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Error parsing the '*Size' or '*MaxDocNum' property, the format is like 'default=<int>;<topicName>,<topicName>=<int>': "
							+ e.getMessage(), e);
		}
	}

	@Override
	public Set<String> getCfgTopics(){
		
		Set<String> topics = new HashSet<String>();
		
		topics.addAll(topicNameToMongo.keySet());
		topics.addAll(topicNameToMaxDocNums.keySet());
		topics.addAll(topicNameToSizes.keySet());
		
		return topics;
	}

	@Override
	public TopicConfig getTopicConfig(String topic) {
		
		return new TopicConfig(
					getKeyOrDefault(topicNameToMongo, topic), 
					getKeyOrDefault(topicNameToSizes, topic), 
					getKeyOrDefault(topicNameToMaxDocNums, topic));
	}

	private <V> V getKeyOrDefault(Map<String, V> map, String topic) {
		
		V result = map.get(topic);
		if(result != null){
			return result;
		}
		return map.get(TOPICNAME_DEFAULT);
	}

	@Override
	protected SwallowConfigArgs doOnConfigChange(String key, String value) {

        if (LION_KEY_MONGO_URLS.equals(key)) {
            topicNameToMongo = parseServerURIString(value, LION_KEY_MONGO_URLS);
            return new SwallowConfigArgs(CHANGED_ITEM.ALL_TOPIC_MONGO_MAPPING);
         } else if (LION_KEY_MSG_CAPPED_COLLECTION_SIZE.equals(key)) {
            topicNameToSizes = parseSizeOrDocNum(value, LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
         } else if (LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM.equals(key)) {
            topicNameToMaxDocNums = parseSizeOrDocNum(value, LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
         }

        return null;
	}

	@Override
	protected boolean interested(String key) {
		
		return interestedKeys.contains(key.trim());
	}

	@Override
	public boolean isSupported() {
		
		return !StringUtils.isEmpty(dynamicConfig.get(LION_KEY_MONGO_URLS) );
	}

}
