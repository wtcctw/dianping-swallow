package com.dianping.swallow.common.internal.whitelist;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.exception.SwallowAlertException;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * 使用时需要注入dynamicConfig，并调用init方法初始化
 * 
 * @author kezhu.wu
 */
public class TopicWhiteList implements ConfigChangeListener {

    private static final Logger logger              = LogManager.getLogger(TopicWhiteList.class);

    private static final String TOPIC_SPLIT      = "\\s*(;|,)\\s*";

    private static final String TOPIC_WHITE_LIST = "swallow.topic.whitelist";

    private Set<String>         topics           = new HashSet<String>();
    
    public static  int  MAX_TOPIC_WHILTE_LIST_DECREASE = 10;//一次最少减少10个topic白名单

    private DynamicConfig       lionDynamicConfig;
    
    
    static{
    	String topicWhiteListDecrease = System.getProperty("MAX_TOPIC_WHILTE_LIST_DECREASE", "10");
    	
    	MAX_TOPIC_WHILTE_LIST_DECREASE = Integer.parseInt(topicWhiteListDecrease);
    }

    public void init() {
        build();

        //监听lion
        lionDynamicConfig.addConfigChangeListener(this);

    }

    public boolean isValid(String topic) {
        return topic != null && topics.contains(topic);
    }

    @Override
    public void onConfigChange(String key, String value) {
    	
    	if(logger.isInfoEnabled()){
    		logger.info("Invoke onConfigChange, key='" + key + "', value='" + value + "'");
    	}
    	
        key = key.trim();
        if (key.equals(TOPIC_WHITE_LIST)) {
            try {
                build();
            } catch (RuntimeException e) {
                logger.error("Error initialize 'topic white list' from lion ", e);
            }
        }
    }

	public void build(){
    	
    	Set<String> newWhiteList = getWhiteList(lionDynamicConfig.get(TOPIC_WHITE_LIST));
    	
    	if(newWhiteList == null || (topics.size() - newWhiteList.size()) > MAX_TOPIC_WHILTE_LIST_DECREASE){
    		
    		String message = "[build][topic decrease too mush]" + topics.size() + "," + newWhiteList.size(); 
    		logger.error("[build][decrease too much]", new SwallowAlertException(message));
    		return;
    	}
    	
    	topics = newWhiteList;
    	
    	if(logger.isInfoEnabled()){
    		logger.info("[build][newWhiteList]" + topics.size() + "," + topics);
    	}
    }
    
    
    protected Set<String> getWhiteList(String whileList) {

        Set<String> _topics = new HashSet<String>();

        if (StringUtils.isEmpty(whileList)) {
        	return null;
        }
        
        String[] topics = whileList.split(TOPIC_SPLIT);
        for (String t : topics) {
            if (!StringUtils.isEmpty(t)) {
                _topics.add(t);
            }
        }

        return _topics;
    }

    public void setLionDynamicConfig(DynamicConfig lionDynamicConfig) {
        this.lionDynamicConfig = lionDynamicConfig;
    }

    public void addTopic(String topic) {
        this.topics.add(topic);
    }
    
    public Set<String>  getTopics(){
    	
    	return new HashSet<String>(topics);
    }

}
