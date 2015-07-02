package com.dianping.swallow.common.internal.config;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl.LionRet;

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午5:57:36
 */
public class TopicConfigTest {

	
	
	@Test
	public void testSub(){
		
		TopicConfig config1 = new TopicConfig("mongodb://192.168.213.143:27018", 1, 2);
		
		TopicConfig config2 = new TopicConfig("mongodb://192.168.213.143:27018", 1, 2);
		
		config1.sub(config2);
		
		Assert.assertNull(config1.getMongoUrl());
		Assert.assertNull(config1.getMax());
		Assert.assertNull(config1.getSize());
		
	}
	
	
	@Test
	public void testJson(){
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		TopicConfig config = jsonBinder.fromJson("{}", TopicConfig.class);
		System.out.println(config);
	}
	
	@Test
	public void testMerge(){
		
		LionRet ret = JsonBinder.getNonEmptyBinder().fromJson("{}", LionRet.class);
		System.out.println(ret);
		
	}
	
	
	@Test
	public void testEquals(){
		
		Integer a = null;
		Integer b = 1;
		
		System.out.println( a == b);
		
	}
}
