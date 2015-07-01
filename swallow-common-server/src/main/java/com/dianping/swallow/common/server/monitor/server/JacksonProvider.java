package com.dianping.swallow.common.server.monitor.server;

import javax.ws.rs.ext.ContextResolver;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * @author mengwenchao
 *
 * 2015年7月1日 下午4:06:05
 */
public class JacksonProvider implements ContextResolver<ObjectMapper>{

	@Override
	public ObjectMapper getContext(Class<?> type) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		
		return mapper;
	}

}
