package com.dianping.swallow.common.internal.message;

import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author mengwenchao
 *
 * 2016年1月22日 上午11:42:12
 */
public class SwallowMessageTest extends AbstractTest{
	
	
	@Test
	public void testSwallowMessge(){
		
		SwallowMessage message = JsonBinder.getNonEmptyBinder().fromJson("{\"internalProperties\":null}", SwallowMessage.class);
		
		
		SwallowMessage message1 = new SwallowMessage();
		String json = JsonBinder.getNonEmptyBinder().toJson(message1);
		
	}
	
	@Test
	public void test(){
		
		Person p = new Person();
		String json = JsonBinder.getNonEmptyBinder().toJson(p);
		
		System.out.println(json);
		
	}
	
	
	@Test
	public void testNull() throws JsonProcessingException{

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		logger.info(mapper.writeValueAsString(new SwallowMessage()));
	}

	class Person {
		
		String name = "lucy";
		Integer    age = 1;
		
	}
}
