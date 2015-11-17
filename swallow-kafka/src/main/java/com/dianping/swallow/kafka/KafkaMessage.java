package com.dianping.swallow.kafka;

import java.nio.ByteBuffer;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:26:18
 */
public class KafkaMessage {
	
	private Long offset;
	
	private byte []message;
	
	public KafkaMessage(Long offset, byte [] message){
		
		this.offset = offset;
		this.message = message;
	}

	public KafkaMessage(long offset, ByteBuffer payload) {
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

	

}
