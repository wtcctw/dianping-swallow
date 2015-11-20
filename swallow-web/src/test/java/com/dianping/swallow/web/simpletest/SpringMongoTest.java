package com.dianping.swallow.web.simpletest;

import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;


/**
 * @author mengwenchao
 *
 * 2015年7月7日 下午4:04:05
 */
public class SpringMongoTest {
	
	
	@Test
	public void testCapped() throws UnknownHostException{
		
//		MongoTemplate mongoTemplate = new MongoTemplate(new Mongo("192.168.213.143", 27018), "swallow-web-test");
		@SuppressWarnings("deprecation")
		MongoTemplate mongoTemplate = new MongoTemplate(new Mongo("192.168.5.10", 27018), "swallow-web-test");
		
		mongoTemplate.dropCollection("test");
		mongoTemplate.createCollection("test", new CollectionOptions(1024*1024, 1024, true));
		
		
	}

}
