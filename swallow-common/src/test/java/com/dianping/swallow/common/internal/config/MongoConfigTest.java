package com.dianping.swallow.common.internal.config;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClientOptions;
import com.mongodb.TaggableReadPreference;

/**
 * @author mengwenchao
 *
 * 2015年5月27日 下午5:55:43
 */
public class MongoConfigTest extends AbstractTest{

	
	@Test
	public void testTag1(){
		
		
		//use:product
		MongoConfig config = new MongoConfig("swallow-mongo-1.properties", null, false);
		MongoClientOptions options = config.buildMongoOptions();
		TaggableReadPreference read = (TaggableReadPreference) options.getReadPreference();

		Assert.assertEquals("secondary", read.getName());
		
		List<DBObject> result = new LinkedList<DBObject>();
		result.add(new BasicDBObject("use", "product"));
		
		equals(result, read.getTagSets());
		
	}
	
	private void equals(List<DBObject> result, List<DBObject> actual) {
		
		Assert.assertEquals(result.size(), actual.size());
	
		for(int i = 0; i<result.size() ; i++){
			
			DBObject left = result.get(i);
			DBObject right = actual.get(i);
			Assert.assertEquals(left, right);
		}
		
	}

	@Test
	public void testTag2(){
		
		//use:product,tag:first;use:product:tag:second
		MongoConfig config = new MongoConfig("swallow-mongo-2.properties", null, false);
		MongoClientOptions options = config.buildMongoOptions();
		TaggableReadPreference read = (TaggableReadPreference) options.getReadPreference();

		Assert.assertEquals("secondaryPreferred", read.getName());

		List<DBObject> result = new LinkedList<DBObject>();
		DBObject tagset1 = new BasicDBObject();
		tagset1.put("use", "product");
		tagset1.put("tag", "first");
		result.add(tagset1);

		DBObject tagset2 = new BasicDBObject();
		tagset2.put("use", "product");
		tagset2.put("tag", "second");
		result.add(tagset2);

		equals(result, read.getTagSets());
	}
	
	@Test
	public void testNormal(){

		MongoConfig config = new MongoConfig("swallow-mongo.properties");
		MongoClientOptions options = config.buildMongoOptions();
		TaggableReadPreference read = (TaggableReadPreference) options.getReadPreference();
		
		Assert.assertEquals(0, read.getTagSets().size());

	}
	
	
	@Test
	public void testOnChange() throws InterruptedException{

		ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);
		final MongoConfig config = new MongoConfig("swallow-mongo.properties");
		scheduled.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				MongoClientOptions options = config.buildMongoOptions();
				System.out.println(options.getReadPreference());
			}
			
		}, 0, 5, TimeUnit.SECONDS);
		
		
//		TimeUnit.SECONDS.sleep(3000);
	}
}
