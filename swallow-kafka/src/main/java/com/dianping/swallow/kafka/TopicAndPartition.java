package com.dianping.swallow.kafka;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:29:54
 */
public class TopicAndPartition {
	
	private String topic;
	
	private Integer partition;

	public TopicAndPartition(String topic){
		this(topic, 0);
	}
	
	public TopicAndPartition(String topic, Integer partition){
		
		this.topic = topic;
		this.partition = partition;
	}

	
	public static TopicAndPartition fromKafka(kafka.common.TopicAndPartition tp){
		
		return new TopicAndPartition(tp.topic(), tp.partition());
	}
	
	public kafka.common.TopicAndPartition toKafka(){
		return new kafka.common.TopicAndPartition(topic, partition);
	}

	
	public String getTopic() {
		return topic;
	}



	public void setTopic(String topic) {
		this.topic = topic;
	}



	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TopicAndPartition)){
			return false;
		}
		
		TopicAndPartition other = (TopicAndPartition) obj;
		
		 if(!(this.topic == null ? other.topic == null : this.topic.equals(other.topic))){
			return false; 
		 }
		
		 if(!(this.partition == null ? other.partition == null : this.partition.equals(other.getPartition()))){
			 return false;
		 }
		
		return true;
	}
	
	@Override
	public int hashCode() {
		
		int hash = 0;
		if(topic != null){
			hash = topic.hashCode();
		}
		
		if(partition != null){
			hash = hash*31 + partition;
		}
		return hash;
	}

	
	@Override
	public String toString() {
		return "[" + topic + ":" + topic + ",partition:" + partition + "]";
	}
}
