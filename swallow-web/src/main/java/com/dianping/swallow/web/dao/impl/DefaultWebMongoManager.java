package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:18
 */
public class DefaultWebMongoManager implements WebMongoManager {

	public 	static final String TOPIC_COLLECTION = "c";
	public 	static final String PRE_MSG = "msg#";
	private static final String PRE_MONGO = "mongodb://";
	private static final String SWALLOW_MONGO = "swallow.mongo.producerServerURI";
	private static final String TOPICNAME_DEFAULT = "default";
	private static final String SWALLOW_W_MONGO = "swallow.mongourl";
	private volatile Map<String, MongoClient> topicNameToMongoMap = new HashMap<String, MongoClient>();
	private List<MongoClient> allReadMongo = new ArrayList<MongoClient>();

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultWebMongoManager.class);

	public DefaultWebMongoManager() {
		initMongoServer();
	}

	private void initMongoServer() {
		String uri = null;
		try {
			uri = ConfigCache.getInstance().getProperty(SWALLOW_W_MONGO);
		} catch (LionException e) {
			e.printStackTrace();
		}
		if (logger.isInfoEnabled()) {
			logger.info(uri);
		}
		try {
			uri = ConfigCache.getInstance().getProperty(SWALLOW_MONGO);
		} catch (LionException e) {
			e.printStackTrace();
		}
		topicNameToMongoMap = parseURIAndCreateTopicMongo(uri.trim()); 
		if (logger.isInfoEnabled()) {
			logger.info(uri);
		}

		try {
			ConfigCache.getInstance().addChange(new ConfigChange() {

				@Override
				public void onChange(String key, String value) {
					onConfigChange(key, value);
				}
			});
		} catch (LionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, MongoClient> getTopicNameToMongoMap() { 
																
		return topicNameToMongoMap;
	}

	@Override
	public MongoTemplate getMessageMongoTemplate(String topicName) {
		MongoClient mongo = this.getMongoClient(topicName);
		return new MongoTemplate(new SimMongoDbFactory(mongo, PRE_MSG
				+ topicName));
	}

	private MongoClient getMongoClient(String topicName) {
		MongoClient mongo = this.topicNameToMongoMap.get(topicName);
		if (mongo == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("topicname '" + topicName
						+ "' do not match any Mongo Server, use default.");
			}
			mongo = this.topicNameToMongoMap.get(TOPICNAME_DEFAULT);
		}
		return mongo;
	}

	@Override
	public List<MongoClient> getAllReadMongo() { // topic name without msg#
		return allReadMongo;
	}

	/**
	 * 解析URI，且创建topic使用的Mongo实例
	 */
	private Map<String, MongoClient> parseURIAndCreateTopicMongo(
			String serverURI) {
		try {
			// 解析uri
			Map<String, List<String>> serverURIToTopicNames = new HashMap<String, List<String>>();
			boolean defaultExists = false;
			for (String topicNamesToURI : serverURI.split(";")) {
				String[] splits = topicNamesToURI.split("=");
				String mongoURI = splits[1];
				String topicNameStr = splits[0];
				List<String> topicNames = new ArrayList<String>();
				for (String topicName : topicNameStr.split(",")) {
					if (TOPICNAME_DEFAULT.equals(topicName)) {
						defaultExists = true;
					}
					topicNames.add(topicName);
				}
				List<String> topicNames0 = serverURIToTopicNames.get(mongoURI);
				if (topicNames0 != null) { // already exist
					topicNames.addAll(topicNames0);
				}
				serverURIToTopicNames.put(mongoURI, topicNames); 
			}
			// 验证uri(default是必须存在的topicName)
			if (!defaultExists) {
				throw new IllegalArgumentException("The '" + ""
						+ "' property must contain 'default' topicName!");
			}
			// 根据uri创建Mongo，放到Map
			HashMap<String, MongoClient> topicNameToMongoMap = new HashMap<String, MongoClient>();
			for (Map.Entry<String, List<String>> entry : serverURIToTopicNames
					.entrySet()) {
				String uri = entry.getKey(); // mongo uri
				List<ServerAddress> replicaSetSeeds = parseUriToAddressList(uri);
				MongoClient mongo = null;
				List<String> topicNames = entry.getValue();
				mongo = getExistsMongo(replicaSetSeeds);
				if (mongo == null) {// 创建mongo实例
					mongo = new MongoClient(replicaSetSeeds);
					if (!allReadMongo.contains(mongo)) // add to allMongo
						allReadMongo.add(mongo);
				}
				for (String topicName : topicNames) {
					topicNameToMongoMap.put(topicName, mongo);
				}
			}
			return topicNameToMongoMap;
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(
					"Error parsing the '*ServerURI' property, the format is '<topicName>,default=<mongoURI>;<topicName>=<mongoURI>': "
							+ e.getMessage(), e);
		}
	}

	/**
	 * 如果已有的map或heartbeatMongo中已经存在相同的地址的Mongo实例，则重复使用
	 */
	private MongoClient getExistsMongo(List<ServerAddress> replicaSetSeeds) {
		MongoClient mongo = null;
		if (this.topicNameToMongoMap != null) {// 如果已有的map中已经存在该Mongo实例，则重复使用
			for (MongoClient m : this.topicNameToMongoMap.values()) {
				if (equalsOutOfOrder(m.getAllAddress(), replicaSetSeeds)) {
					mongo = m;
					break;
				}
			}
		}

		return mongo;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean equalsOutOfOrder(List list1, List list2) {
		if (list1 == null || list2 == null) {
			return false;
		}
		return list1.containsAll(list2) && list2.containsAll(list1);
	}

	private List<ServerAddress> parseUriToAddressList(String uri) {
		uri = uri.trim();
		String schema = PRE_MONGO;
		if (uri.startsWith(schema)) { // 兼容老各式uri
			uri = uri.substring(schema.length());
		}
		String[] hostPortArr = uri.split(",");
		List<ServerAddress> result = new ArrayList<ServerAddress>();
		for (int i = 0; i < hostPortArr.length; i++) {
			String[] pair = hostPortArr[i].split(":");
			try {
				result.add(new ServerAddress(pair[0].trim(), Integer
						.parseInt(pair[1].trim())));
			} catch (Exception e) {
				throw new IllegalArgumentException(
						e.getMessage()
								+ ". Bad format of mongo uri："
								+ uri
								+ ". The correct format is mongodb://<host>:<port>,<host>:<port>",
						e);
			}
		}
		return result;
	}

	public synchronized void onConfigChange(String key, String value) {
		if (logger.isInfoEnabled()) {
			logger.info("onChange() called.");
		}
		value = value.trim();
		try {
			if (SWALLOW_MONGO.equals(key)) {
				this.topicNameToMongoMap = parseURIAndCreateTopicMongo(value);
				Thread.sleep(5000);// DAO可能正在使用旧的Mongo，故等候5秒，才执行关闭操作
			}
		} catch (Exception e) {
			logger.error(
					"Error occour when reset config from Lion, no config property would changed :"
							+ e.getMessage(), e);
		}
	}

}