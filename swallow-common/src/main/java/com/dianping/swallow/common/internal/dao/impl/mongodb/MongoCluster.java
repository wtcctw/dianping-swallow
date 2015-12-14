package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractCluster;
import com.dianping.swallow.common.internal.exception.SwallowAlertException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:25:27
 */
public class MongoCluster extends AbstractCluster{
	
	public static final String MSG_PREFIX = "msg#";
	public static final String ACK_PREFIX = "ack#";
	public static final String BACKUP_MSG_PREFIX = "b_m#";
	public static final String BACKUP_ACK_PREFIX = "b_a#";

	private static final String DEFAULT_COLLECTION_NAME = "c";

	private boolean messageCollectionCapped = true;

	public static final String schema = "mongodb://";
	
	private MongoClientOptions mongoOptions;
	
	private MongoClient mongoClient;

	private final Map<DB, Byte> collectionExistsSign = new ConcurrentHashMap<DB, Byte>();

	public MongoCluster(MongoClientOptions mongoOptions, String address){
		
		super(address);
		
		this.mongoOptions = mongoOptions;
	}
	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		mongoClient = new MongoClient(toMongoSeeds(getSeeds()), mongoOptions);
		
	}

	@Override
	protected void doDispose() throws Exception {
		super.doDispose();
		
		if(mongoClient != null){
			mongoClient.close();
		}
	}
	
	private List<ServerAddress> toMongoSeeds(List<InetSocketAddress> seeds) {
		
		List<ServerAddress>  result = new ArrayList<ServerAddress>(seeds.size());
		
		for(InetSocketAddress socketAddress : seeds){
			
			result.add(new ServerAddress(socketAddress));
		}
		
		return result;
	}

	
	
	public MongoClient getMongoClient(){

		return mongoClient;
	}

	public MongoClientOptions getMongoOptions() {
		return mongoOptions;
	}

	public DBCollection getMessageCollection(String topicName) {
		
		return getMessageCollection(topicName, null);
	}

	
	public DBCollection getMessageCollection(String topicName, String consumerId) {

		List<DBObject> index = new LinkedList<DBObject>();
		Integer size = -1;
		Integer max = 0;
		String dbName = getMessageDbName(topicName, consumerId);

		index.add(new BasicDBObject(MongoMessageDAO.ID, -1));
		if (consumerId == null) {
			
			TopicConfig config = swallowConfig.getTopicConfig(topicName);
			if (messageCollectionCapped) {
				size = getSize(config);
				max = getMax(config);
			}
		} else {
			index.add(new BasicDBObject(MongoMessageDAO.ORIGINAL_ID, -1));
			if (messageCollectionCapped) {
				size = AbstractSwallowConfig.DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE;
				max = AbstractSwallowConfig.DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM;
			}
		}
		return getCollection(size, max, dbName, index);
	}

	private String getMessageDbName(String topicName, String consumerId) {

		if (consumerId == null) {
			return MSG_PREFIX + topicName;
		}
		return BACKUP_MSG_PREFIX + topicName + "#" + consumerId;
	}


	private int getMax(TopicConfig config) {
		
		return config.getMax() == null ? swallowConfig.defaultTopicConfig().getMax() : config.getMax();
	}

	private int getSize(TopicConfig config) {
		
		return config.getSize() == null ? swallowConfig.defaultTopicConfig().getSize() : config.getSize();
	}

	public DBCollection getCollection(Integer size, Integer cappedCollectionMaxDocNum, String dbName, DBObject... indexDBObjects) {
		
		List<DBObject> index = new LinkedList<DBObject>();
		for (DBObject dbObject : indexDBObjects) {
			index.add(dbObject);
		}
		return getCollection(size, cappedCollectionMaxDocNum, dbName,
				index);
	}

	private DBCollection getCollection(Integer size,
			Integer cappedCollectionMaxDocNum, String dbName, List<DBObject> indexDBObjects) {

		DB db = mongoClient.getDB(dbName);

		// 从DB实例获取Collection(因为只有一个Collection，所以名字均叫做c),如果不存在，则创建)
		DBCollection collection = null;
		if (!collectionExists(db)) {// 从缓存检查default collection
									// 存在的标识，避免db.collectionExists的调用
			synchronized (db) {
				if (!collectionExists(db)
						&& !db.collectionExists(DEFAULT_COLLECTION_NAME)) {
					collection = createColletcion(db, DEFAULT_COLLECTION_NAME, size, cappedCollectionMaxDocNum, indexDBObjects);
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

	
	private DBCollection createColletcion(DB db, String collectionName,
			Integer size, Integer cappedCollectionMaxDocNum,
			List<DBObject> indexDBObjects) {

		if (logger.isInfoEnabled()) {
			logger.info("[createColletcion]" + "db:" + db.getName() +",name:" + collectionName
					+ ", size:" + size + ", cappedCollectionMaxDocNum:"
					+ cappedCollectionMaxDocNum);
		}

		DBObject options = new BasicDBObject();
		if (size != null && size > 0) {
			options.put("capped", true);
			options.put("size", size * AbstractSwallowConfig.MILLION);
			if (cappedCollectionMaxDocNum > 0) {
				options.put("max", cappedCollectionMaxDocNum * AbstractSwallowConfig.MILLION);
			}
		}else{
			logger.error("size not correct:" + db.getName() + "," + size, new SwallowAlertException("CollectionSize not right"));
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

	private boolean collectionExists(DB db) {
		return collectionExistsSign.containsKey(db);
	}

	private void markCollectionExists(DB db) {
		collectionExistsSign.put(db, Byte.MAX_VALUE);
	}

	private void markCollectionNotExists(DB db) {
		collectionExistsSign.remove(db);
	}

	public void cleanMessageCollection(String topicName, String consumerId) {

		if (logger.isInfoEnabled()) {
			logger.info("[cleanMessageCollection]" + topicName + "," + consumerId);
		}

		DBCollection collection = getMessageCollection(topicName, consumerId);

		boolean isCapped = collection.isCapped();

		// 此处不能只dropCollection，如果只dropCollection会导致db大小不停增加
		dropDatabase(topicName, consumerId);

		markCollectionNotExists(mongoClient.getDB(getMessageDbName(topicName, consumerId)));

		collection = getMessageCollection(topicName, consumerId);

		if (isCapped != collection.isCapped()) {
			logger.warn("[cleanMessageCollection][capped type change]"
					+ isCapped + "," + collection.isCapped());
		}
	}

	public void cleanAckCollection(String topicName, String consumerId,
			boolean isBackup) {

		if (logger.isInfoEnabled()) {
			logger.info("[cleanAckCollection]" + topicName + "," + consumerId
					+ "," + isBackup);
		}

		String dbName = getAckDbName(topicName, consumerId, isBackup);
		mongoClient.dropDatabase(dbName);

		markCollectionNotExists(mongoClient.getDB(dbName));
		getAckCollection(topicName, consumerId, isBackup);
	}

	private String getAckDbName(String topicName, String consumerId, boolean isBackup) {

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

		String dbName = getAckDbName(topicName, consumerId, isBackup);

		DBObject index = new BasicDBObject(MongoMessageDAO.ID, -1);
		return getCollection(AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_SIZE, 
									AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM, dbName, index);
	}


	private void dropDatabase(String topicName, String consumerId) {

		String dbName = getMessageDbName(topicName, consumerId);
		mongoClient.dropDatabase(dbName);
	}

	@Override
	public List<InetSocketAddress> allServers() {
		try{
			List<ServerAddress> allAddresses = mongoClient.getServerAddressList();
			return toInetSocketAddress(allAddresses);
		}catch(MongoException e){
			logger.error("[allServers]" + getSeeds(), e);
			return getSeeds();
		}
	}

	private List<InetSocketAddress> toInetSocketAddress(List<ServerAddress> allAddresses) {
		
		List<InetSocketAddress>  addresses = new ArrayList<InetSocketAddress>(allAddresses.size());
		
		for(ServerAddress serverAddress : allAddresses){
			addresses.add(new InetSocketAddress(serverAddress.getHost(), serverAddress.getPort()));
		}
		
		return addresses;
	}

	@Override
	protected String addressExample() {
		return schema + "<host>:<port>,<host>:<port>";
	}

	@Override
	protected String getSchema() {
		return schema;
	}

	@Override
	public MessageDAO<?> createMessageDao() {
		return new MongoMessageDAO(this);
	}

}
