package com.dianping.swallow.common.internal.dao.impl.mongodb;



import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.AbstractMongoTest;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author mengwenchao
 *
 * 2015年10月13日 上午11:41:42
 */
public class MongoContainerTest extends AbstractMongoTest{
	
	private MongoContainer container = new MongoContainer(MongoClientOptions.builder().build());
	
	@Test
	public void testCreate(){
		
		MongoClient mongo1 = container.getMongo("mongodb://192.168.214.143:27018");
		MongoClient mongo2 = container.getMongo("mongodb://192.168.214.143:27018");
		
		Assert.assertEquals(mongo1, mongo2);
		Assert.assertEquals(1, container.getAllMongo().size());

		MongoClient mongo3 = container.getMongo("mongodb://192.168.214.143:27018,192.168.214.143:27019");
		MongoClient mongo4 = container.getMongo("mongodb://192.168.214.143:27019,192.168.214.143:27018");

		Assert.assertNotEquals(mongo1, mongo3);
		Assert.assertEquals(mongo3, mongo4);
		
		
		Assert.assertEquals(2, container.getAllMongo().size());
		
	}

}
