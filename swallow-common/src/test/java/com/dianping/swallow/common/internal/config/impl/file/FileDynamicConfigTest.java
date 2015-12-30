package com.dianping.swallow.common.internal.config.impl.file;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.impl.file.FileDynamicConfig;

/**
 * @author mengwenchao
 *
 * 2015年12月30日 下午5:37:23
 */
public class FileDynamicConfigTest extends AbstractTest{

	/**
	a.1=1
	a.2=2
	b.3=3
	b.4=4
	 */
	private FileDynamicConfig config = new FileDynamicConfig("LocalDynamicConfig.properties");
	
	
	@Test
	public void testGet(){
		
		Assert.assertEquals("1", config.get("a.1"));
		Assert.assertEquals("4", config.get("b.4"));
		
		Map<String, String> properties = config.getProperties("a");
		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("1", properties.get("a.1"));
		Assert.assertEquals("2", properties.get("a.2"));
		
	}
	
	

}
