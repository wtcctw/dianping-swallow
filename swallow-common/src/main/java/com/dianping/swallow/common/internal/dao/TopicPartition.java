package com.dianping.swallow.common.internal.dao;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午5:48:50
 */
public class TopicPartition {
	
	private final String topic;
	
	/**
	 * partition为null，意味着默认无partition
	 */
	private final Integer partition;

	public TopicPartition(String topic){
		this(topic, null);
	}
	
	public TopicPartition(String topic, Integer partition){
		
		this.topic = topic;
		this.partition = partition;
		 
		
	}
	
	public String getTopic() {
		return topic;
	}
	public Integer getPartition() {
		return partition;
	}

}
