package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:38:10
 */
public class MongoClientTest extends AbstractTest{
	
	private DefaultMongoManager mongoClient = new DefaultMongoManager("swallow.mongo.producerServerURI");
	
	@Test
	public void testParse(){
		
		String server1 = "mongodb://127.0.0.1";
		String server2 = "mongodb://127.0.0.2";
		String topic1 = "topic1";
		String topic2 = "topic2";
		String def = "default=mongodb://127.0.0.111;";
		
		String [] urls = new String[]{
				def + "topic1=" +server1+ ";topic2=" + server2,
				def + "topic1=" +server1+ ";\ntopic2=" + server2,
				def + "topic1=" +server1+ ";\rtopic2=" + server2,
				def + "topic1=" +server1+ ";\n\rtopic2=" + server2,
				def + "topic1 =" +server1+ ";\n\rtopic2 =" + server2,
				def + " topic1 = " +server1+ ";\n\rtopic2 = " + server2
		};
		
		for(String url : urls){
			
			if(logger.isInfoEnabled()){
				logger.info("[testParse][url]" + url);
			}
			Map<String, List<String>> serverURIToTopicNames = mongoClient.parseServerURIString(url);
			Assert.assertEquals(topic1, serverURIToTopicNames.get(server1).get(0));
			Assert.assertEquals(topic2, serverURIToTopicNames.get(server2).get(0));
		}
		
		
	}

}
