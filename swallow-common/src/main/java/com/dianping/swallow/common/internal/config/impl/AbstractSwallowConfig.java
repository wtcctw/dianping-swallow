package com.dianping.swallow.common.internal.config.impl;


import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservableLifecycle;
import com.dianping.swallow.common.internal.util.PropertiesUtils;

/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午5:11:40
 */
public abstract class AbstractSwallowConfig extends AbstractObservableLifecycle implements SwallowConfig, ConfigChangeListener{
	
	public static final int DEFAULT_CAPPED_COLLECTION_SIZE = 10;
	
	public static final int DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM = 1;

	public static final int DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE = 50;
	
	public static final int DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM = 1;

	public static final int MILLION = 1024 * 1024;
	
	private final String 	LION_CONFIG_FILENAME          = PropertiesUtils.getProperty("SWALLOW.MONGO.LION.CONFFILE", "swallow-mongo-lion.properties");
	
	public static final String 	TOPICNAME_DEFAULT             = "default";

	protected String heartBeatMongo;

	protected static final String LION_KEY_HEARTBEAT_SERVER_URI = "swallow.mongo.heartbeatServerURI";

	protected DynamicConfig                 dynamicConfig;

	public AbstractSwallowConfig(){
		
		dynamicConfig = new LionDynamicConfig(LION_CONFIG_FILENAME);
	}
	
	@Override
	protected void doInitialize() throws Exception {
		
		super.doInitialize();
		
		dynamicConfig.addConfigChangeListener(this);
		loadConfig();
	}

	@Override
	protected void doDispose() throws Exception {
		
		dynamicConfig.removeConfigChangeListener(this);
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
	public void onConfigChange(String key, String value) {

		if(logger.isDebugEnabled()){
			logger.debug("[onConfigChange]" + key + ":" + value);
		}
		
		SwallowConfigArgs args = null;
		
		if (LION_KEY_HEARTBEAT_SERVER_URI.equals(key)) {
			if(logger.isInfoEnabled()){
				logger.info("[onConfigChange]" + key + ":" + value);
			}
			heartBeatMongo = value.trim();
			args = new SwallowConfigArgs(CHANGED_ITEM.HEART_BEAT_MONGO);
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

	protected abstract boolean interested(String key);

	protected abstract SwallowConfigArgs doOnConfigChange(String key, String value);


	public static class SwallowConfigArgs{
		
		private CHANGED_ITEM item;
		
		private String  topic;
		
		private CHANGED_BEHAVIOR behavior = CHANGED_BEHAVIOR.UPDATE;

		public SwallowConfigArgs(CHANGED_ITEM item){
			this.item = item;
		}

		public SwallowConfigArgs(CHANGED_ITEM item, String topic){
			
			this(item, topic, CHANGED_BEHAVIOR.UPDATE);
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
		
		@Override
		public String toString() {
			return item + "," + topic + "," + behavior;
		}
		
	}
	
	public enum CHANGED_ITEM{
		
		/**
		 * 全部配置更新
		 */
		ALL_TOPIC_MONGO_MAPPING,
		
		/**
		 * 特定topic对应配置
		 */
		TOPIC_MONGO,

		
		/**
		 * 心跳配置更新 
		 */
		HEART_BEAT_MONGO
		
	}

	public enum CHANGED_BEHAVIOR{
		
		UPDATE,
		ADD,
		DELETE
	}

}
