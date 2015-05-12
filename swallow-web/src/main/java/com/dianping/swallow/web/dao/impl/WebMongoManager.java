package com.dianping.swallow.web.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;



/**
 * @author mingdongli
 * 2015年4月8日 下午6:31:13
 */
public interface WebMongoManager {
	
	MongoTemplate getMessageMongoTemplate(String topicName);
	
	List< MongoClient> getAllReadMongo();
	
	Map<String, MongoClient> getTopicNameToMongoMap();
	
}
