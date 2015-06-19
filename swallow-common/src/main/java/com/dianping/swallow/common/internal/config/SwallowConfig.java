package com.dianping.swallow.common.internal.config;

import java.util.Set;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.util.StringUtils;


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

	String getHeartBeatMongo();
	
	boolean isSupported();
	
	public static class TopicConfig implements Cloneable{
		
		private String mongoUrl;
		private Integer size;
		private Integer max;
		
		public TopicConfig(){
			
		}
		
		public TopicConfig(String mongoUrl, int size, int max){
			
			this.mongoUrl = mongoUrl;
			this.size = size;
			this.max = max;
			
		}

		public String getMongoUrl() {
			return mongoUrl;
		}

		public Integer getSize() {
			return size;
		}

		public Integer getMax() {
			return max;
		}
		
		public boolean valid(){
			
			return !StringUtils.isEmpty(mongoUrl)
					|| size != null && size > 0
					|| max != null && max >0;
		}
		
		public void merge(TopicConfig defaultConfig){
			
			if(StringUtils.isEmpty(mongoUrl)){
				mongoUrl = defaultConfig.mongoUrl;
			}
			
			if(size == null){
				size = defaultConfig.size;
			}
			
			if( max == null ){
				max = defaultConfig.max;
			}
		}
		
		/**
		 * 减法，减去默认配置，得到自身最小化的配置
		 * @param config
		 */
		public void sub(TopicConfig config){
			
			if(mongoUrl != null && mongoUrl.equals(config.getMongoUrl())){
				mongoUrl = null;
			}
			
			if(size != null && size.equals(config.getSize())){
				size = null;
			}
			
			if(max != null && max.equals(config.getMax())){
				max = null;
			}
		}
		
		@Override
		public String toString() {
			return toJson();
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			
			TopicConfig topicCfg = (TopicConfig) super.clone();
			
			return topicCfg;
		}
		
		public String toJson(){
			return JsonBinder.getNonEmptyBinder().toJson(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TopicConfig)){
				return false;
			}
			TopicConfig cmp = (TopicConfig) obj;
			
			return (mongoUrl == null? cmp.mongoUrl == null : mongoUrl.equals(cmp.mongoUrl))
					&& ( size == cmp.size )
					&& ( max == cmp.max );
		}
	}

}
