package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigImpl;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:38:10
 */
public class DefaultMongoManagerTest extends AbstractTest{
	
	
	private DefaultMongoManager mongoManager;

	private String []topics = new String[]{"topic1", "topic2", "topic3"};
	
	
	/**
		swallow.topiccfg.default={"mongoUrl":"mongodb://192.168.213.143:27018","size":100,"max":100}
		swallow.topiccfg.topic1={"size":200,"max":200}
		swallow.topiccfg.topic2={}
		swallow.topiccfg.topic3={"mongoUrl":"mongodb://192.168.213.143:27118","size":101,"max":102}	 
		* @throws Exception
	 */
	@Before
	public void beforeDefaultMongoManagerTest() throws Exception{
		
		System.setProperty("SWALLOW.MONGO.LION.CONFFILE", "swallow-mongo-createmongo.properties");
		
		mongoManager = new DefaultMongoManager();
		mongoManager.setSwallowConfig(new SwallowConfigImpl());
		mongoManager.initialize();
		
		for(String topic : topics){
			mongoManager.cleanMessageCollection(topic, null);
		}
	}
	
	@Test
	public void testMongo(){
		
		String json = JsonBinder.getNonEmptyBinder().toPrettyJson(mongoManager.getStatus());
		System.out.println(json);

		Map<String, MongoStatus> result = JsonBinder.getNonEmptyBinder().fromJson(json, new TypeReference<Map<String, MongoStatus>>() {});
		
		System.out.println(result.get("default").getClass());
		
	}

	@Test
	public void testCreateMongo(){
		if(!EnvUtil.isDev()){
			return;
		}
		
		DBCollection collection1 = mongoManager.getMessageCollection("topic1");
		checkOk(collection1, 200, 200);
		
		DBCollection collection2 = mongoManager.getMessageCollection("topic2");
		checkOk(collection2, 100, 100);
		
		
		
	}

	private void checkOk(DBCollection col, int size, int max) {
		
		Assert.assertTrue(col.isCapped());
		
		CommandResult result = col.getStats();
		long realSize = result.getLong("storageSize");
		long realMax = result.getLong("max");

		Assert.assertTrue((realSize / (size * AbstractSwallowConfig.MILLION)) == 1 );
		Assert.assertTrue((realMax / (max * AbstractSwallowConfig.MILLION)) == 1 );
		
		Assert.assertEquals(ajustExpectedSize(size * AbstractSwallowConfig.MILLION), realSize);
		Assert.assertEquals(max * AbstractSwallowConfig.MILLION, realMax);
	}

	private Object ajustExpectedSize(long size) {
		
		return ((size / 4096) + 1) *4096;
	}

}
