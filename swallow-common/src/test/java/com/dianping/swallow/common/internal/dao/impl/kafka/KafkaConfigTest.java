package com.dianping.swallow.common.internal.dao.impl.kafka;


import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.config.impl.lion.LionUtilImpl;
import com.dianping.swallow.common.internal.util.EnvUtil;

/**
 * @author mengwenchao
 *
 * 2015年11月18日 上午11:01:35
 */
public class KafkaConfigTest {
	
	
	@Test
	public void test(){
		
		KafkaConfig config = new KafkaConfig("swallow-kafka-test1.properties", "", false);
		
		Assert.assertEquals(false, config.isReadFromMaster());
		Assert.assertEquals(5001, config.getSoTimeout());
		Assert.assertEquals(4, config.getFetchRetryCount());
		Assert.assertEquals(2*1024*1024 + 1, config.getFetchSize());
		Assert.assertEquals(5001, config.getMaxWait());
		
		Assert.assertEquals(101, config.getMaxConnectionPerHost());
		Assert.assertEquals(51, config.getMaxIdlePerHost());
		Assert.assertEquals(false, config.isBlockWhenExhausted());
		Assert.assertEquals(1001, config.getMaxWaitMillis());
	}
	
	
	@Test
	public void testAlpha(){
		
		if(!EnvUtil.isAlpha()){
			return;
		}
		
		LionUtilImpl lionUtilImpl = new LionUtilImpl();
		
		int soTimeout = (int) (Integer.MAX_VALUE * Math.random()); 
		
		lionUtilImpl.createOrSetConfig("swallow.kafkaconfig.soTimeout", String.valueOf(soTimeout));
		
		KafkaConfig config = new KafkaConfig("swallow-kafka-test1.properties", "", true);
		
		Assert.assertEquals(soTimeout, config.getSoTimeout());
		
		
	}


}
