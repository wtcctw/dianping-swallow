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
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.Mongo;
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

	public static final String SWALLOW_STATS_MONGO_URL_KEY = "swallow.mongourl";

	public static final String SWALLOW_MONGO_ADDRESS_FILE = "swallow-mongo-lion.properties";

	public static final String SWALLOW_MONGO_CONFIG_FILE = "swallow-mongo.properties";

	private Mongo mongo;

	private DynamicConfig dynamicConfig;

	@PostConstruct
	public void getMongo() {

		dynamicConfig = new LionDynamicConfig(SWALLOW_MONGO_ADDRESS_FILE);
		String mongoUrl = dynamicConfig.get(SWALLOW_STATS_MONGO_URL_KEY);
		MongoConfig config = new MongoConfig(SWALLOW_MONGO_CONFIG_FILE);
		mongo = new MongoClient(MongoUtils.parseUriToAddressList(mongoUrl),
				config.buildMongoOptions());
		if (logger.isInfoEnabled()) {
			logger.info("[getMongo]" + mongo);
		}
	}

	@Bean(name = "statisMongoTemplate")
	public MongoTemplate getStatisMongoTemplate() {

		return createMongoTemplate(statsMongoDbName);
	}

	@SuppressWarnings("deprecation")
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
