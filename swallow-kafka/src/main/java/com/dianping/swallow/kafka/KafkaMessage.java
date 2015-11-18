package com.dianping.swallow.kafka;

import java.nio.ByteBuffer;

import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:26:18
 */
public class KafkaMessage {
	
	private Long offset;
	
	private byte []message;
	
	private TopicAndPartition tp;
	
	public KafkaMessage(TopicAndPartition tp, Long offset, byte [] message){
		
		this.tp = tp;
		this.offset = offset;
		this.message = message;
	}

	public KafkaMessage(TopicAndPartition tp, long offset, ByteBuffer payload) {
		
		this.tp = tp;
		this.offset = offset;
		this.message = new byte[payload.remaining()];
		payload.get(message);
		
	}

	public Long getOffset() {
		return offset;
	}

	public byte [] getMessage() {
		return message;
	}

	public <T> T deserializer(Deserializer<T> deserializer){
		
		return deserializer.deserialize(tp.getTopic(), message);
	}

	public TopicAndPartition getTp() {
		return tp;
	}

}
