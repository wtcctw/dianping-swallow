package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.common.internal.config.GroupConfig;
import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;

/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午5:11:40
 */
public abstract class AbstractSwallowServerConfig extends AbstractSwallowConfig implements SwallowServerConfig{
	
	public static final int DEFAULT_CAPPED_COLLECTION_SIZE = 10;
	
	public static final int DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM = 1;

	public static final int DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE = 50;
	
	public static final int DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM = 1;

	public static final long MILLION = 1024 * 1024;
	
	protected String heartBeatMongo;

	protected static final String LION_KEY_HEARTBEAT_SERVER_URI = "swallow.mongo.heartbeatServerURI";

	public AbstractSwallowServerConfig(){
		
	}
	
	@Override
	protected void doInitialize() throws Exception {
		
		super.doInitialize();
		
		loadConfig();
	}

	@Override
	public GroupConfig getGroupConfig(String group) {
		
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public TopicConfig defaultTopicConfig() {
		
		return getTopicConfig(TOPICNAME_DEFAULT);
	}
	
	@Override
	protected void doDispose() throws Exception {
		
		super.doDispose();
	}
	
	protected void loadConfig() throws Exception {
		
		heartBeatMongo = dynamicConfig.get(LION_KEY_HEARTBEAT_SERVER_URI).trim();
		doLoadConfig();
	}

	protected abstract void doLoadConfig();

	
	@Override
	public String getHeartBeatMongo() {
		
		return heartBeatMongo;
	}

	@Override
	public void onConfigChange(String key, String value) throws Exception {

		SwallowConfigArgs args = null;
		
		if (LION_KEY_HEARTBEAT_SERVER_URI.equals(key)) {
			if(logger.isInfoEnabled()){
				logger.info("[onConfigChange]" + key + ":" + value);
			}
			heartBeatMongo = value.trim();
			args = new SwallowConfigArgs(CHANGED_ITEM.HEART_BEAT_STORE);
        }else{
        	if(interested(key)){
    			if(logger.isInfoEnabled()){
    				logger.info("[onConfigChange]" + key + ":" + value);
    			}
        		args = doOnConfigChange(key, value);
        	}
        }
		
		if(args != null){
			updateObservers(args);
		}
	}

	
	@Override
	public int getOrder() {
		return ORDER;
	}
	
	protected abstract boolean interested(String key);

	protected abstract SwallowConfigArgs doOnConfigChange(String key, String value) throws SwallowConfigException;


	public static class SwallowConfigArgs{
		
		private CHANGED_ITEM item;
		
		private String  topic;
		
		private CHANGED_BEHAVIOR behavior = CHANGED_BEHAVIOR.UPDATE;
		
		private TopicConfig oldConfig = null;;

		public SwallowConfigArgs(CHANGED_ITEM item){
			this.item = item;
		}

		public SwallowConfigArgs(CHANGED_ITEM item, String topic, TopicConfig oldConfig){
			
			this(item, topic, CHANGED_BEHAVIOR.UPDATE);
			this.oldConfig = oldConfig;
		}
		
		public SwallowConfigArgs(CHANGED_ITEM item, String topic, CHANGED_BEHAVIOR behavior){
			
			this.item = item;
			this.topic = topic;
			this.behavior = behavior;
		}

		
		public CHANGED_ITEM getItem(){
			return item;
		}
		
		public String getTopic(){
			return topic; 
		}

		public CHANGED_BEHAVIOR getBehavior() {
			return behavior;
		}
		
		public TopicConfig getOldConfig() {
			return oldConfig;
		}

		@Override
		public String toString() {
			return item + "," + topic + "," + behavior;
		}
		
	}
	
	public enum CHANGED_ITEM{
		
		/**
		 * 全部配置更新
		 */
		ALL_TOPIC_STORE_MAPPING,
		
		/**
		 * 特定topic对应配置
		 */
		TOPIC_STORE,

		
		/**
		 * 心跳配置更新 
		 */
		HEART_BEAT_STORE
		
	}

	public enum CHANGED_BEHAVIOR{
		
		UPDATE,
		ADD,
		DELETE
	}

}
