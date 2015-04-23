package com.dianping.swallow.web.controller.utils;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mengwenchao
 *
 * 2015年4月17日 上午10:35:55
 */
public class JacksonConverterFactory implements FactoryBean<MappingJackson2HttpMessageConverter>{

	public JacksonConverterFactory(){
		
	}
	
	@Override
	public MappingJackson2HttpMessageConverter getObject() throws Exception {
		
		MappingJackson2HttpMessageConverter  converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = JsonBinder.getNonEmptyBinder().getObjectMapper();
		converter.setObjectMapper(objectMapper);;
		return converter;
	}

	@Override
	public Class<?> getObjectType() {
		
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
