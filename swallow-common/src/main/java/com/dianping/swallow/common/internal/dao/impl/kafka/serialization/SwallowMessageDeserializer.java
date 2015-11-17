package com.dianping.swallow.common.internal.dao.impl.kafka.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.dianping.swallow.common.internal.codec.Codec;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月8日 下午9:20:04
 */
public class SwallowMessageDeserializer extends AbstractSwallowMessageCodec implements Deserializer<SwallowMessage>{

	
	private JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder(); 

	@Override
	public SwallowMessage deserialize(String topic, byte[] data) {
		
		return jsonBinder.fromJson(new String(data, Codec.DEFAULT_CHARSET), SwallowMessage.class);
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	@Override
	public void close() {
		
	}

}
