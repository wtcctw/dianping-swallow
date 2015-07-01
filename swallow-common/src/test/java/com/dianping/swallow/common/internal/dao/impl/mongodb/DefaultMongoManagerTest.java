package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigImpl;
import com.fasterxml.jackson.core.type.TypeReference;

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
		mongoManager.initialize();
		
	}
	
	@Test
	public void testMongo(){
		
		System.out.println(mongoManager.getStatus());
		
		String json = JsonBinder.getNonEmptyBinder().toPrettyJson(mongoManager.getStatus());
		System.out.println(json);

		Map<String, MongoStatus> result = JsonBinder.getNonEmptyBinder().fromJson(json, new TypeReference<Map<String, MongoStatus>>() {});
		
		System.out.println(result.get("default").getClass());
		
	}
	

}
