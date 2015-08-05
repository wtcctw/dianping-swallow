package com.dianping.swallow.web.dao.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.MongoConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * @author mengwenchao
 *
 *         2015年4月18日 下午9:31:12
 */
@Configuration
public class MongoTemplateFactory {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String MAP_KEY_DOT_REPLACEMENT = "__";

	@Value("${swallow.web.mongodbname.stats}")
	private String statsMongoDbName;

	@Value("${swallow.web.mongodbname.topic}")
	private String topicMongoDbName;
	
	@Value("${swallow.web.mongodbname.alarmstats}")
	private String alarmStatsMongoDbName;

	public static final String SWALLOW_STATS_MONGO_URL_KEY = "swallow.mongourl";

	public static final String SWALLOW_MONGO_ADDRESS_FILE = "swallow-mongo-lion.properties";

	public static final String SWALLOW_MONGO_CONFIG_FILE = "swallow-web-mongo.properties";

	private MongoClient mongo;

	private MongoConfig config;
	private DynamicConfig dynamicConfig;

	@PostConstruct
	public void getMongo() {

		dynamicConfig = new LionDynamicConfig(SWALLOW_MONGO_ADDRESS_FILE);
		String mongoUrl = dynamicConfig.get(SWALLOW_STATS_MONGO_URL_KEY);
		config = new MongoConfig(SWALLOW_MONGO_CONFIG_FILE);
		
		mongo = new MongoClient(MongoUtils.parseUriToAddressList(mongoUrl), config.buildMongoOptions());
		if (logger.isInfoEnabled()) {
			logger.info("[getMongo]" + mongo);
		}
	}

	@Bean(name = "statisMongoTemplate")
	public MongoTemplate getStatisMongoTemplate() {

		MongoTemplate statisMongoTemplate = createMongoTemplate(statsMongoDbName);
		
		//create collection
		
		long size = Long.parseLong(dynamicConfig.get("swallow.mongo.web.stais.cappedCollectionSize"));
		long max =  Long.parseLong(dynamicConfig.get("swallow.mongo.web.stais.cappedCollectionMaxDocNum"));
		createCappedCollection(statisMongoTemplate, ProducerMonitorData.class.getSimpleName(), size, max);
		createCappedCollection(statisMongoTemplate, ConsumerMonitorData.class.getSimpleName(), size, max);
		
		return statisMongoTemplate;
	}
	
	@Bean(name = "alarmStatsMongoTemplate")
	public MongoTemplate getAlarmStatisMongoTemplate() {
		return new MongoTemplate(new SimMongoDbFactory(mongo, alarmStatsMongoDbName));
	}
	
	private synchronized void createCappedCollection(MongoTemplate mongoTemplate, String collectionName, long size, long max) {
		
		if(!mongoTemplate.collectionExists(collectionName)){			
			if(logger.isInfoEnabled()){
				logger.info("[createCappedCollection][createCollection]" + collectionName + ",size:" + size + ",max:" + max);
			}
			mongoTemplate.getDb().createCollection(collectionName, getCappedOptions(size, max));
		}
		
		
	}

	private DBObject getCappedOptions(long size, long max) {

		DBObject options = new BasicDBObject();
		if (size > 0) {
			options.put("capped", true);
			options.put("size", size * AbstractSwallowConfig.MILLION);
			if (max > 0) {
				options.put("max", max * AbstractSwallowConfig.MILLION);
			}
		}
		return options;
	}

	@Bean(name = "topicMongoTemplate")
	public MongoTemplate getTopicMongoTemplate() {

		return new MongoTemplate(new SimMongoDbFactory(mongo, topicMongoDbName));
	}

	private MongoTemplate createMongoTemplate(String mongoDbName) {

		if (logger.isInfoEnabled()) {
			logger.info("[createMongoTemplate]" + mongoDbName);
		}
		MongoTemplate template = new MongoTemplate(mongo, mongoDbName);
		initMongoTemplate(template);
		return template;
	}

	private void initMongoTemplate(MongoTemplate template) {

		MongoConverter converter = template.getConverter();
		if (converter instanceof MappingMongoConverter) {
			if (logger.isInfoEnabled()) {
				logger.info("[initMongoTemplate]" + MAP_KEY_DOT_REPLACEMENT);
			}
			((MappingMongoConverter) converter)
					.setMapKeyDotReplacement(MAP_KEY_DOT_REPLACEMENT);
		}

		if (logger.isInfoEnabled()) {
			logger.info("[initMongoTemplate][setWriteResultChecking]exception");
		}
		template.setWriteResultChecking(WriteResultChecking.EXCEPTION);

		if (logger.isInfoEnabled()) {
			logger.info("[initMongoTemplate][set write concern]safe");
		}
		template.setWriteConcern(WriteConcern.SAFE);

	}
	
}
