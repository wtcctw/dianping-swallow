package com.dianping.swallow.web.dao.impl;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:18
 */
@Component
public class DefaultWebMongoManager implements WebMongoManager {

	public static final String TOPIC_COLLECTION = "c";
	public static final String PRE_MSG = "msg#";

	@Autowired
	private DefaultMongoManager mongoManager;

	public void setMongoManager(DefaultMongoManager mongoManager) {
		this.mongoManager = mongoManager;
	}

//	public void initMongoServer() {
//	}

	@SuppressWarnings("deprecation")
	@Override
	public MongoTemplate getMessageMongoTemplate(String topicName) {
		Mongo mongo = this.getMongoClient(topicName);
		MongoTemplate template = new MongoTemplate(new SimMongoDbFactory(mongo, PRE_MSG+ topicName));
		template.setReadPreference(mongo.getReadPreference());
		return template;
	}

	private Mongo getMongoClient(String topicName) {
		
		return mongoManager.getMongo(topicName);
	}

	@Override
	public Collection<MongoClient> getAllReadMongo() {
		return mongoManager.getAllMongo();
	}

}