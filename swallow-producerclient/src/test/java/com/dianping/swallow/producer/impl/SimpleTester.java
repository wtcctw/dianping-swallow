package com.dianping.swallow.producer.impl;

import java.io.IOException;

import org.junit.Test;

import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

/**
 * @author mengwenchao
 *
 * 2015年1月23日 上午11:43:01
 */
public class SimpleTester {
	
	@Test
	public void testSerial() throws JsonGenerationException, JsonMappingException, IOException{

		Optional<String> myString = Optional.of("testString");

	    ObjectMapper objectMapper = new ObjectMapper();
	    String jsonString = objectMapper.writeValueAsString(myString);
	    System.out.println(jsonString);
	}
	
	@Test
	public void testVersion(){
		System.out.println(SwallowHelper.getVersion());
	}
	
}
