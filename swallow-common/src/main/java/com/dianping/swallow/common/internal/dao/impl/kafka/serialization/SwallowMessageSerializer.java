package com.dianping.swallow.common.internal.dao.impl.kafka.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月8日 下午9:20:04
 */
public class SwallowMessageSerializer implements Serializer<SwallowMessage>{

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	@Override
	public byte[] serialize(String topic, SwallowMessage data) {
		return null;
	}

	@Override
	public void close() {
		
	}

}
