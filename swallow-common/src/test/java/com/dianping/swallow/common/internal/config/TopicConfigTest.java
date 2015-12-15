package com.dianping.swallow.common.internal.config;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.message.JsonDeserializedException;

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午5:57:36
 */
public class TopicConfigTest {


	@Test
	public void testOldMongo(){
	
		
		String mongo = "mongodb://10.1.115.11:27018,10.1.115.12:27018";
		Integer size = 1000, max = 2000;
				
		String mongoConfig = "{\"mongoUrl\":\""+mongo+"\",\"size\":" +size+ ", \"max\" : " + max + "}";
		
		TopicConfig config = TopicConfig.fromJson(mongoConfig);
		
		System.out.println(config);
		
		Assert.assertEquals(mongo, config.getStoreUrl());
		Assert.assertEquals(max, config.getMax());
		Assert.assertEquals(size, config.getSize());
	}

	@Test( expected = JsonDeserializedException.class)
	public void testBadJson(){
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		jsonBinder.fromJson("{\"xx\" : xx\"}", TopicConfig.class);
	}

	
	
	@Test
	public void testJson(){
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		TopicConfig config = jsonBinder.fromJson("{}", TopicConfig.class);
		
		Assert.assertEquals(new TopicConfig(), config);
		
		config = jsonBinder.fromJson(null, TopicConfig.class);
		Assert.assertNull(config);

		config = jsonBinder.fromJson("", TopicConfig.class);
		Assert.assertNull(config);

		
		config = new TopicConfig("mongodb://url", 100, 200, TOPIC_TYPE.EFFICIENCY_FIRST);
		
		String json = config.toJson();
		System.out.println(json);
		
		TopicConfig configBack = TopicConfig.fromJson(json);
		
		Assert.assertEquals(config, configBack);
				
		
		
	}
	

	@Test
	public void testAllValid(){
		
		TopicConfig topicConfig = new TopicConfig();
		Assert.assertFalse(topicConfig.allValid());
		
		topicConfig.setMax(1);
		Assert.assertFalse(topicConfig.allValid());

		topicConfig.setSize(1);
		Assert.assertFalse(topicConfig.allValid());
		
		topicConfig.setStoreUrl("mongodb://10.1.1.1");
		Assert.assertTrue(topicConfig.allValid());
	} 
	
	
	@Test
	public void testEquals(){
		
		Integer a = null;
		Integer b = 1;
		
		System.out.println( a == b);
		
	}
}
