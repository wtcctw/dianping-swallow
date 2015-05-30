package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.Mongo;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:18
 */
public class DefaultWebMongoManager implements WebMongoManager {

	public static final String TOPIC_COLLECTION = "c";
	public static final String PRE_MSG = "msg#";
	private static final String SWALLOW_MONGO = "swallow.mongo.producerServerURI";
	private static final String TOPICNAME_DEFAULT = "default";
	private static final String SWALLOW_W_MONGO = "swallow.mongourl";
	private volatile Map<String, Mongo> topicNameToMongoMap = new ConcurrentHashMap<String, Mongo>();

	DefaultMongoManager mongoManager;

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultWebMongoManager.class);

	public void setMongoManager(DefaultMongoManager mongoManager) {
		this.mongoManager = mongoManager;
	}

	public void initMongoServer() {
		String uri = null;
		try {
			uri = ConfigCache.getInstance().getProperty(SWALLOW_W_MONGO);
		} catch (LionException e) {
			logger.error("Error when read " + SWALLOW_W_MONGO + " from lion.",
					e);
		}
		try {
			uri = ConfigCache.getInstance().getProperty(SWALLOW_MONGO);
		} catch (LionException e) {
			logger.error("Error when read " + SWALLOW_MONGO + " from lion.", e);
		}
		topicNameToMongoMap = mongoManager.parseURIAndCreateTopicMongo(uri
				.trim());
		try {
			ConfigCache.getInstance().addChange(new ConfigChange() {

				@Override
				public void onChange(String key, String value) {
					onConfigChange(key, value);
				}
			});
		} catch (LionException e) {
			logger.error("Error when addChange of lion.", e);
		}
	}

	@Override
	public Map<String, Mongo> getTopicNameToMongoMap() {

		return topicNameToMongoMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	public MongoTemplate getMessageMongoTemplate(String topicName) {
		Mongo mongo = this.getMongoClient(topicName);
		MongoTemplate template = new MongoTemplate(new SimMongoDbFactory(mongo, PRE_MSG+ topicName));
		template.setReadPreference(mongo.getReadPreference());
		return template;
	}

	private Mongo getMongoClient(String topicName) {
		Mongo mongo = this.topicNameToMongoMap.get(topicName);
		if (mongo == null) {
			logger.debug("topicname '" + topicName
					+ "' do not match any Mongo Server, use default.");
			mongo = this.topicNameToMongoMap.get(TOPICNAME_DEFAULT);
		}
		return mongo;
	}

	@Override
	public List<Mongo> getAllReadMongo() { // topic name without msg#
		return new ArrayList<Mongo>(this.topicNameToMongoMap.values());
	}

	public synchronized void onConfigChange(String key, String value) {
		value = value.trim();
		try {
			if (SWALLOW_MONGO.equals(key)) {
				this.topicNameToMongoMap = mongoManager
						.parseURIAndCreateTopicMongo(value);
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			logger.error(
					"Error occour when reset config from Lion, no config property would changed :"
							+ e.getMessage(), e);
		}
	}

}