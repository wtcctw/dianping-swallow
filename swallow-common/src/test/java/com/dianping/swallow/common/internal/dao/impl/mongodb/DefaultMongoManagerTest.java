package com.dianping.swallow.common.internal.dao.impl.mongodb;


import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigImpl;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:38:10
 */
public class DefaultMongoManagerTest extends AbstractTest{
	
	
	private DefaultMongoManager mongoManager;
	
	@Before
	public void beforeDefaultMongoManagerTest() throws Exception{
		
		mongoManager = new DefaultMongoManager();
		mongoManager.setSwallowConfig(new SwallowConfigImpl());
		
	}
	
	@Test
	public void testMongo(){
		
		
		
	}
	

}
