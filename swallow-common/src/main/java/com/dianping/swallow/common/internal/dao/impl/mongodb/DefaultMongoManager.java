package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.config.MongoConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig.SwallowConfigArgs;
import com.dianping.swallow.common.internal.dao.MongoManager;
import com.dianping.swallow.common.internal.lifecycle.Ordered;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * 管理Mongo以及Collection的获取
 * @author mengwenchao
 *
 * 2015年6月12日 下午3:57:42
 */
public class DefaultMongoManager extends AbstractLifecycle implements MongoManager, Observer{

	public static final String MSG_PREFIX = "msg#";
	public static final String ACK_PREFIX = "ack#";
	public static final String BACKUP_MSG_PREFIX = "b_m#";
	public static final String BACKUP_ACK_PREFIX = "b_a#";

	private static final Logger logger = LoggerFactory.getLogger(DefaultMongoManager.class);

	private static final String MONGO_CONFIG_FILENAME = "swallow-mongo.properties";
	private static final String DEFAULT_COLLECTION_NAME = "c";

	private final Map<DB, Byte> collectionExistsSign = new ConcurrentHashMap<DB, Byte>();

	private volatile Map<String, MongoClient> topicNameToMongoMap;

	private volatile MongoClient heartbeatMongo;

	private SwallowConfig swallowConfig;

	private MongoClientOptions mongoOptions;

	private boolean messageCollectionCapped = true;
	
	private Set<MongoClient>  mongos = new HashSet<MongoClient>();

	public DefaultMongoManager() {
		this(null);
	}

	public DefaultMongoManager(String mongoConfigLionSuffix) {

		MongoConfig config = new MongoConfig(MONGO_CONFIG_FILENAME, mongoConfigLionSuffix);
		mongoOptions = config.buildMongoOptions();
		if (logger.isInfoEnabled()) {
			logger.info("MongoOptions=" + mongoOptions.toString());
		}
	}

	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		swallowConfig.addObserver(this);
		swallowConfig.initialize();
		
		loadSwallowConfig();
	}
	
	
	@Override
	protected void doDispose() throws Exception {
		
		closeAllMongo();
		swallowConfig.dispose();
		super.doDispose();
	}
	
	private void closeAllMongo() {
		
		if(logger.isInfoEnabled()){
			logger.info("[closeAllMongo]");
		}
		
		for(MongoClient mongo : topicNameToMongoMap.values()){
			mongo.close();
		}
		heartbeatMongo.close();
	}

	private void loadSwallowConfig() {

		try {
			createTopicMongo();
			createHeartbeatMongo();
		} catch (Exception e) {
			throw new IllegalArgumentException("[loadSwallowConfig]", e);
		}
	}

	private void createHeartbeatMongo() {

		String serverURI = swallowConfig.getHeartBeatMongo();
		List<ServerAddress> replicaSetSeeds = MongoUtils.parseUriToAddressList(serverURI);
		heartbeatMongo = createOrUseExistingMongo(replicaSetSeeds);
		if (logger.isInfoEnabled()) {
			logger.info("[createHeartbeatMongo]" + heartbeatMongo);
		}
	}

	private synchronized MongoClient createOrUseExistingMongo(List<ServerAddress> replicaSetSeeds) {
		
		if(logger.isInfoEnabled()){
			logger.info("[createOrUseExistingMongo]" + replicaSetSeeds);
		}
		
		for(MongoClient mongo : mongos){
			
			if(seedIn(mongo.getServerAddressList(), replicaSetSeeds)){
				if(logger.isDebugEnabled()){
					logger.debug("[createOrUseExistingMongo][use exist mongo]");
				}
				return mongo;
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[createMongo]" + replicaSetSeeds);
		}
		
		MongoClient mongo = new MongoClient(replicaSetSeeds, mongoOptions);
		mongos.add(mongo);
		return mongo;
	}

	private void closeMongo(MongoClient mongo) {
		
		if (logger.isInfoEnabled()) {
			logger.info("[closeMongo]" + mongo);
		}
		
		boolean contains = mongos.remove(mongo);
		if(!contains){
			
			String errorMessage = "[close unexist mongo]" + mongo + "," + mongos;
			logger.error(errorMessage);
			Cat.logError(new IllegalStateException(errorMessage));
		}
		mongo.close();
	}

	public void createTopicMongo() {
		
		try {
			topicNameToMongoMap = new HashMap<String, MongoClient>();
			for (String topicName : swallowConfig.getCfgTopics()){
				createTopicMongo(topicName);
			}
			if(logger.isInfoEnabled()){
				logger.info("[createTopicMongo]" + topicNameToMongoMap);
			}
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Error parsing the '*ServerURI' property, the format is '<topicName>,default=<mongoURI>;<topicName>=<mongoURI>': " + e.getMessage(), e);
		}
	}

	/**
	 * 返回之前的MongoClient
	 * @param topicName
	 * @return
	 */
	private MongoClient createTopicMongo(String topicName) {
		
		TopicConfig topicConfig = swallowConfig.getTopicConfig(topicName);
		String uri = topicConfig.getMongoUrl();
		List<ServerAddress> replicaSetSeeds = MongoUtils.parseUriToAddressList(uri);
		MongoClient mongo = null;
		mongo = createOrUseExistingMongo(replicaSetSeeds);
		
		MongoClient oldMongoClient = topicNameToMongoMap.put(topicName, mongo);
		if (logger.isInfoEnabled()) {
			logger.info("[createreateTopicMongo]["+topicName+"]" + mongo);
		}
		
		return oldMongoClient;
	}

	@Override
	public void update(Observable observable, Object rawArgs) {

		SwallowConfigArgs args = (SwallowConfigArgs) rawArgs;
		
		if(logger.isInfoEnabled()){
			logger.info("[update]" + args);
		}
		
		switch (args.getItem()) {

		case ALL_TOPIC_MONGO_MAPPING:
			Map<String, MongoClient> oldTopicNameToMongoMap = this.topicNameToMongoMap;
			createTopicMongo();
			closeUnuseMongo(oldTopicNameToMongoMap.values(), this.topicNameToMongoMap.values(), heartbeatMongo);
			break;
		case TOPIC_MONGO:
			MongoClient oldClient = createTopicMongo(args.getTopic());
			if(oldClient != null){
				closeUnuseMongo(oldClient, topicNameToMongoMap.values(), heartbeatMongo);
			}
			break;
		case HEART_BEAT_MONGO:
			MongoClient oldMongo = this.heartbeatMongo;
			createHeartbeatMongo();
			closeUnuseMongo(oldMongo, this.topicNameToMongoMap.values(), this.heartbeatMongo);
			break;
		default:
			logger.warn("[update][unknown item]" + args);
		}
	}

	private void closeUnuseMongo(Collection<MongoClient> oldMongos, Collection<MongoClient> curMongos, MongoClient curMongo) {
		oldMongos.removeAll(curMongos);
		oldMongos.remove(curMongo);
		for (MongoClient unuseMongo : oldMongos) {
			if (unuseMongo != null) {
				closeMongo(unuseMongo);
			}
		}
	}

	private void closeUnuseMongo(MongoClient oldMongo, Collection<MongoClient> curMongos, MongoClient curMongo) {
		if (!curMongos.contains(oldMongo) && oldMongo != curMongo) {
			closeMongo(oldMongo);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean seedIn(List allAddress, List seeds) {

		boolean result = false;
		
		if (allAddress != null && allAddress != null) {
			result = allAddress.containsAll(seeds);
		}
		if(logger.isDebugEnabled()){
			logger.debug("[seedIn][" + result + "]" + allAddress + "," + seeds);
		}
		return result;
	}

	public DBCollection getMessageCollection(String topicName) {
		return getMessageCollection(topicName, null);
	}

	public DBCollection getMessageCollection(String topicName, String consumerId) {

		MongoClient mongo = getMongo(topicName);
		List<DBObject> index = new LinkedList<DBObject>();
		int size = -1;
		int max = 0;
		String dbName = getMessageDbName(topicName, consumerId);

		index.add(new BasicDBObject(MessageDAOImpl.ID, -1));
		if (consumerId == null) {
			
			TopicConfig config = swallowConfig.getTopicConfig(topicName);
			if (messageCollectionCapped) {
				size = config.getSize();
				max = config.getMax();
			}
		} else {
			index.add(new BasicDBObject(MessageDAOImpl.ORIGINAL_ID, -1));
			if (messageCollectionCapped) {
				size = AbstractSwallowConfig.DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE;
				max = AbstractSwallowConfig.DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM;
			}
		}
		return getCollection(mongo, size, max, dbName, index);
	}

	private String getMessageDbName(String topicName, String consumerId) {

		if (consumerId == null) {
			return MSG_PREFIX + topicName;
		}
		return BACKUP_MSG_PREFIX + topicName + "#" + consumerId;
	}

	private void dropDatabase(String topicName, String consumerId) {

		String dbName = getMessageDbName(topicName, consumerId);
		MongoClient mongo = getMongo(topicName);
		mongo.dropDatabase(dbName);
	}

	@Override
	public MongoClient getMongo(String topicName) {

		MongoClient mongo = this.topicNameToMongoMap.get(topicName);
		if (mongo == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("topicname '" + topicName
						+ "' do not match any MongoClient Server, use default.");
			}
			mongo = this.topicNameToMongoMap.get(AbstractSwallowConfig.TOPICNAME_DEFAULT);
		}
		return mongo;
	}

	@Override
	public void cleanMessageCollection(String topicName, String consumerId) {

		if (logger.isInfoEnabled()) {
			logger.info("[cleanMessageCollection]" + topicName + ","
					+ consumerId);
		}

		DBCollection collection = getMessageCollection(topicName, consumerId);

		boolean isCapped = collection.isCapped();

		// 此处不能只dropCollection，如果只dropCollection会导致db大小不停增加
		dropDatabase(topicName, consumerId);

		markCollectionNotExists(getMongo(topicName).getDB(
				getMessageDbName(topicName, consumerId)));

		collection = getMessageCollection(topicName, consumerId);

		if (isCapped != collection.isCapped()) {
			logger.warn("[cleanMessageCollection][capped type change]"
					+ isCapped + "," + collection.isCapped());
		}
	}

	@Override
	public void cleanAckCollection(String topicName, String consumerId,
			boolean isBackup) {

		if (logger.isInfoEnabled()) {
			logger.info("[cleanAckCollection]" + topicName + "," + consumerId
					+ "," + isBackup);
		}

		MongoClient mongo = getMongo(topicName);

		String dbName = getAckDbName(topicName, consumerId, isBackup);
		mongo.dropDatabase(dbName);

		markCollectionNotExists(mongo.getDB(dbName));
		getAckCollection(topicName, consumerId, isBackup);
	}

	private String getAckDbName(String topicName, String consumerId,
			boolean isBackup) {

		String dbName = null;

		if (!isBackup) {
			dbName = ACK_PREFIX + topicName + "#" + consumerId;
		} else {
			dbName = BACKUP_ACK_PREFIX + topicName + "#" + consumerId;
		}
		return dbName;
	}

	public DBCollection getAckCollection(String topicName, String consumerId) {

		return getAckCollection(topicName, consumerId, false);
	}

	public DBCollection getAckCollection(String topicName, String consumerId,
			boolean isBackup) {

		MongoClient mongo = getMongo(topicName);
		String dbName = getAckDbName(topicName, consumerId, isBackup);

		DBObject index = new BasicDBObject(AckDAOImpl.MSG_ID, -1);
		return getCollection(mongo, AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_SIZE, 
									AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM, dbName, index);
	}

	public DBCollection getHeartbeatCollection(String ip) {

		MongoClient mongo = this.heartbeatMongo;
		return this.getCollection(mongo, AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_SIZE,
				AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM, "heartbeat#" + ip,
				new BasicDBObject(HeartbeatDAOImpl.TICK, -1));
	}

	private DBCollection getCollection(MongoClient mongo, int size,
			int cappedCollectionMaxDocNum, String dbName,
			DBObject... indexDBObjects) {
		List<DBObject> index = new LinkedList<DBObject>();
		for (DBObject dbObject : indexDBObjects) {
			index.add(dbObject);
		}
		return getCollection(mongo, size, cappedCollectionMaxDocNum, dbName,
				index);
	}

	private DBCollection getCollection(MongoClient mongo, int size,
			int cappedCollectionMaxDocNum, String dbName,
			List<DBObject> indexDBObjects) {
		// 根据topicname从Mongo实例从获取DB
		DB db = mongo.getDB(dbName);

		// 从DB实例获取Collection(因为只有一个Collection，所以名字均叫做c),如果不存在，则创建)
		DBCollection collection = null;
		if (!collectionExists(db)) {// 从缓存检查default collection
									// 存在的标识，避免db.collectionExists的调用
			synchronized (db) {
				if (!collectionExists(db)
						&& !db.collectionExists(DEFAULT_COLLECTION_NAME)) {
					collection = createColletcion(db, DEFAULT_COLLECTION_NAME,
							size, cappedCollectionMaxDocNum, indexDBObjects);
				}
				markCollectionExists(db);// 缓存default collection
											// 存在的标识，避免db.collectionExists的调用
			}
			if (collection == null) {
				collection = db.getCollection(DEFAULT_COLLECTION_NAME);
			}
		} else {
			collection = db.getCollection(DEFAULT_COLLECTION_NAME);
		}
		return collection;
	}

	private boolean collectionExists(DB db) {
		return collectionExistsSign.containsKey(db);
	}

	private void markCollectionExists(DB db) {
		collectionExistsSign.put(db, Byte.MAX_VALUE);
	}

	private void markCollectionNotExists(DB db) {
		collectionExistsSign.remove(db);
	}

	private DBCollection createColletcion(DB db, String collectionName,
			int size, int cappedCollectionMaxDocNum,
			List<DBObject> indexDBObjects) {

		if (logger.isInfoEnabled()) {
			logger.info("[createColletcion]" + "name:" + collectionName
					+ ", size:" + size + ", cappedCollectionMaxDocNum:"
					+ cappedCollectionMaxDocNum);
		}

		DBObject options = new BasicDBObject();
		if (size > 0) {
			options.put("capped", true);
			options.put("size", size * AbstractSwallowConfig.MILLION);
			if (cappedCollectionMaxDocNum > 0) {
				options.put("max", cappedCollectionMaxDocNum * AbstractSwallowConfig.MILLION);
			}
		}
		try {
			DBCollection collection = db.createCollection(collectionName,
					options);
			if (indexDBObjects != null) {
				for (DBObject indexDBObject : indexDBObjects) {
					collection.createIndex(indexDBObject);
					if (logger.isInfoEnabled()) {
						logger.info("Ensure index " + indexDBObject
								+ " on colleciton " + collection);
					}
				}
			}
			return collection;
		} catch (MongoException e) {
			if (e.getMessage() != null
					&& e.getMessage().indexOf("collection already exists") >= 0) {
				// collection already exists
				logger.warn(e.getMessage() + ":the collectionName is "
						+ collectionName);
				return db.getCollection(collectionName);
			} else {
				// other exception, can not connect to mongo etc, should abort
				throw e;
			}
		}
	}

	@Override
	public MongoClientOptions getMongoOptions() {
		return mongoOptions;
	}

	@Override
	public int getMongoCount() {

		return topicNameToMongoMap.values().size();
	}

	public SwallowConfig getSwallowConfig() {
		return swallowConfig;
	}

	public void setSwallowConfig(SwallowConfig swallowConfig) {
		this.swallowConfig = swallowConfig;
	}

	public Collection<MongoClient> getAllMongo(){
		
		return Collections.unmodifiableCollection(mongos);
	}
	
	@Override
	public int getOrder() {
		
		return Ordered.FIRST;
	}
}
