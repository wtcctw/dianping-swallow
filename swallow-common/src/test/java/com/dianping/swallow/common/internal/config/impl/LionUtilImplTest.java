package com.dianping.swallow.common.internal.config.impl;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.lion.Constants;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.util.http.HttpMethod;

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午2:45:25
 */
public class LionUtilImplTest extends AbstractTest implements ConfigChange{
	
	
	private LionUtilImpl lionUtil = new LionUtilImpl(2L);
	
	private String 	 TEST_KEY = "unittest";
	
	private String keyValue;
	
	private ConfigCache cc = ConfigCache.getInstance();
	
	
	@Before
	public void beforeLionUtilImplTest(){
		
		cc.addChange(this);
		
	}

	

	@Test
	public void testGetConfigsPost(){
		
		checkConfig(HttpMethod.POST);
	}

	@Test
	public void testGetConfigsGet(){

		checkConfig(HttpMethod.GET);
		
	}
	
	private void checkConfig(HttpMethod httpMethod) {
		
		String value = UUID.randomUUID().toString();
		String newKeys[] = new String[]{"", "a", "a.b", ".a", ".a.b"};
		
		for(String addKey : newKeys){
			
			lionUtil.createOrSetConfig(TEST_KEY + addKey, value + addKey, httpMethod);
		}
		
		Map<String, String> cfgs = lionUtil.getCfgs(TEST_KEY);
		
		Assert.assertEquals(newKeys.length, cfgs.size());
		
		for(String addKey : newKeys){
			
			Assert.assertEquals(value + addKey, cfgs.get(LionUtilImpl.getRealKey(TEST_KEY + addKey)));	
		}
		


		
	}



	@Test
	public void testCreateOrSetConfig(){
		
		String value = UUID.randomUUID().toString();
		lionUtil.createOrSetConfig(TEST_KEY, value);

		keyValue = cc.getProperty("swallow." + TEST_KEY);
		
		Assert.assertEquals(value, keyValue);
		
		
		value = UUID.randomUUID().toString();
		lionUtil.createOrSetConfig(TEST_KEY, value);

		sleep(100);
		//configlistener
		Assert.assertEquals(value, keyValue);

	}
	
	@Test
	public void testLion(){
		
		ConfigCache cc = ConfigCache.getInstance();
		String group = cc.getAppenv(Constants.KEY_SWIMLANE);
		
		System.out.println(group);
		
		
	}

	@Override
	public void onChange(String key, String value) {
		
		if(key.equals(LionUtilImpl.getRealKey(TEST_KEY))){
			keyValue = value;
		}
	}

}
