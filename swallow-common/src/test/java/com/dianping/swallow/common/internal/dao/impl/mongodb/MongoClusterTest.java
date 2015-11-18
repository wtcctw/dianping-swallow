package com.dianping.swallow.common.internal.dao.impl.mongodb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigImpl;
import com.dianping.swallow.common.internal.dao.impl.AbstractMongoTest;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:38:10
 */
public class MongoClusterTest extends AbstractMongoTest{
	
	
	private MongoCluster mongoCluster;

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
		
		mongoCluster = new MongoCluster(new MongoConfig("swallow-mongo.properties").buildMongoOptions(), getMongoAddress());
		
		mongoCluster.setSwallowConfig(createSwallowConfig());
		mongoCluster.initialize();
		
		for(String topic : topics){
			mongoCluster.cleanMessageCollection(topic, null);
		}
	}
	
	private SwallowConfig createSwallowConfig() throws Exception {
		
		SwallowConfig config = new SwallowConfigImpl();
		config.initialize();
		
		return config;
	}

	@Test
	public void testCreateMongo(){
		if(!EnvUtil.isDev()){
			return;
		}
		
		DBCollection collection1 = mongoCluster.getMessageCollection("topic1");
		checkOk(collection1, 200, 200);
		
		DBCollection collection2 = mongoCluster.getMessageCollection("topic2");
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
