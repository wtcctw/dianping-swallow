package com.dianping.swallow.common.internal.dao.impl.kafka.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.dianping.swallow.common.internal.codec.Codec;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * 默认采用json序列化
 * TODO 优化序列化算法
 * @author mengwenchao
 *
 * 2015年11月8日 下午9:20:04
 */
public class SwallowMessageSerializer extends AbstractSwallowMessageCodec implements Serializer<SwallowMessage>{

	private JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder(); 
	

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	@Override
	public byte[] serialize(String topic, SwallowMessage data) {
		
		return jsonBinder.toJson(data).getBytes(Codec.DEFAULT_CHARSET);
	}

	@Override
	public void close() {
		
	}

}
