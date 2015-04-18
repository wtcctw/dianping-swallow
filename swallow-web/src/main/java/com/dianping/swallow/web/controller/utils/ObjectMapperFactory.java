package com.dianping.swallow.web.controller.utils;

import org.springframework.beans.factory.FactoryBean;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mengwenchao
 *
 * 2015年4月17日 上午10:35:55
 */
public class ObjectMapperFactory implements FactoryBean<ObjectMapper>{

	@Override
	public ObjectMapper getObject() throws Exception {
		
		return JsonBinder.getNonEmptyBinder().getObjectMapper();
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
