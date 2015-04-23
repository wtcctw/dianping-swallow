package com.dianping.swallow.web.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dianping.lion.client.ConfigCache;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:04:18
 */
public class MongoManager {

	public  static final String 				TOPIC_COLLECTION 				= "c";
	public  static final String 				PRE_MSG 						= "msg#";
	private static final String 				PRE_MONGO 						= "mongodb://";
	private static final String 				SWALLOW_MONGO 					= "swallow.mongo.producerServerURI";
	private static final String 				TOPICNAME_DEFAULT 				= "default";
	private static final String 				SWALLOW_W_MONGO 				= "swallow.mongourl";
	
	private volatile Map<String, MongoClient>   topicNameToMongoMap 			= new HashMap<String, MongoClient>();
	private List<MongoClient>                   allReadMongo 					= new ArrayList<MongoClient>();
	private MongoClient              			writeMongo;


	private static MongoManager instance;

	private static synchronized void synInit() {
		if (instance == null)
			instance = new MongoManager();
	}

	public static MongoManager getInstance() {
		if (instance == null) {
			synInit();
		}
		return instance;
	}

	private MongoManager() {
		initMongoServer();
	}

	private void initMongoServer() {
		String uri;
		uri = ConfigCache.getInstance().getProperty(SWALLOW_W_MONGO);
		writeMongo = new MongoClient(parseUriToWriteAddressList(uri));  //write mongo
		
		uri = ConfigCache.getInstance().getProperty(SWALLOW_MONGO);
		topicNameToMongoMap = parseURIAndCreateTopicMongo(uri.trim());  //read mongo
	}

	public Map<String, MongoClient> getTopicNameToMongoMap() {  //topic name without msg#
		return topicNameToMongoMap;
	}
	
	public List< MongoClient> getAllReadMongo() {  //topic name without msg#
		return allReadMongo;
	}
	
	public MongoClient getWriteMongo() {  //topic name without msg#
		return writeMongo;
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
				if (topicNames0 != null) {  //already exist
					topicNames.addAll(topicNames0);
				}
				serverURIToTopicNames.put(mongoURI, topicNames);  //mongoURI are not handled
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
				String uri = entry.getKey();  //mongo uri
				List<ServerAddress> replicaSetSeeds = parseUriToAddressList(uri);
				MongoClient mongo = null;
				List<String> topicNames = entry.getValue();
				mongo = getExistsMongo(replicaSetSeeds);
				if (mongo == null) {// 创建mongo实例
					mongo = new MongoClient(replicaSetSeeds);
					if(!allReadMongo.contains(mongo))  //add to allMongo
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
	
	private List<ServerAddress> parseUriToWriteAddressList(String uri){
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

}