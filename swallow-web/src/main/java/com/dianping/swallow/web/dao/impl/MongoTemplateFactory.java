package com.dianping.swallow.web.dao.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;

import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * @author mengwenchao
 *
 * 2015年4月18日 下午9:31:12
 */
@Configuration
public class MongoTemplateFactory {
	
	@Value("${swallow.web.mongodbname.stats}")
	private String statsMongoDbName;
	
	private static final String SWALLOW_STATS_MONGO_URL_KEY = "swallow.mongourl";
	
	private Mongo  mongo;
	
	private DynamicConfig dynamicConfig;
	
	@PostConstruct
	public void getMongo(){
		
		dynamicConfig = new LionDynamicConfig("swallow-mongo-lion.properties");
		String mongoUrl = dynamicConfig.get(SWALLOW_STATS_MONGO_URL_KEY);
		mongo = new MongoClient(MongoUtils.parseUriToAddressList(mongoUrl));
		
		
	}
	
	@Bean( name = "statisMongoTemplate" )
	public MongoTemplate getStatisMongoTemplate(){
		
		return createMongoTemplate(statsMongoDbName);
	}

	private MongoTemplate createMongoTemplate(String mongoDbName) {
		
		MongoTemplate template = new MongoTemplate(mongo, mongoDbName);
		template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		return template;
	}

}
