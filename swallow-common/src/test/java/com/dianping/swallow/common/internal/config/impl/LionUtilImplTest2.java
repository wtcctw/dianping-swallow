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

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午2:45:25
 */
public class LionUtilImplTest2 extends AbstractTest implements ConfigChange{
	
	
	private LionUtilImpl lionUtil = new LionUtilImpl(2L);
	
	private String 	 TEST_KEY = "unittest2";
	
	private String keyValue;
	
	private ConfigCache cc = ConfigCache.getInstance();
	
	
	@Before
	public void beforeLionUtilImplTest(){
		
		cc.addChange(this);
		
	}

	@Test
	public void testGetKey(){

		String result = lionUtil.getValue("swallow.broker.notify.devMode");
		System.out.println(result);
		Assert.assertEquals(result, "true");

		result = lionUtil.getValue("swallow.broker.notify.smsTo");
		System.out.println(result);
		Assert.assertEquals(result, "15921096896");

		lionUtil.createOrSetConfig("swallow.topiccfg1.lionapi-get-alpha-apply-topic0", "testenv");
		lionUtil.createOrSetConfig("swallow.topiccfg1.lionapi-get-alpha-apply-topic0", "alphapost","post", "alpha");
		lionUtil.createOrSetConfig("swallow.topiccfg1.lionapi-get-alpha-apply-topic0", "alphaget","get", "alpha");
		lionUtil.createOrSetConfig("swallow.topiccfg1.lionapi-get-alpha-apply-topic0", "testcreate","post", null);

	}
	
	//@Test
	public void testGetConfigs(){

		String value = "swallow" + UUID.randomUUID().toString();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < 20; ++i){
			stringBuilder.append(value);
		}
		value = stringBuilder.toString();
		String newKeys[] = new String[]{"", "a", "a.b", ".a", ".a.b"};
		
		for(String addKey : newKeys){
			
			lionUtil.createOrSetConfig(TEST_KEY + addKey, value + addKey, "get", null);
		}
		
		Map<String, String> cfgs = lionUtil.getCfgs(TEST_KEY);
		
		Assert.assertEquals(newKeys.length, cfgs.size());
		
		for(String addKey : newKeys){
			
			Assert.assertEquals(value + addKey, cfgs.get(LionUtilImpl.getRealKey(TEST_KEY + addKey)));	
		}
		

		
	}
	
	//@Test
	public void testCreateOrSetConfig(){
		
		String value = UUID.randomUUID().toString();
		lionUtil.createOrSetConfig(TEST_KEY, value, "get", null);

		keyValue = cc.getProperty("swallow." + TEST_KEY);
		
		Assert.assertEquals(value, keyValue);
		
		
		value = UUID.randomUUID().toString();
		
		lionUtil.createOrSetConfig(TEST_KEY, value, "post", null);

		sleep(100);
		//configlistener
		Assert.assertEquals(value, keyValue);

	}
	
	//@Test
	public void testPut(){
		
		String value = "put method "  + UUID.randomUUID().toString();
		lionUtil.createOrSetConfig(TEST_KEY, value, "get", null);

		keyValue = cc.getProperty("swallow." + TEST_KEY);
		
		Assert.assertEquals(value, keyValue);


		value = "put method " + UUID.randomUUID().toString();
		try{
			lionUtil.createOrSetConfig(TEST_KEY, value, "put", null);
		}catch(Exception e){
			Assert.assertEquals(e.getMessage(), "illegal type :put");
		}

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
