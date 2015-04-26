package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.MongoConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.dao.MongoManager;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class DefaultMongoManager implements ConfigChangeListener, MongoManager {

   private static final String           MSG_PREFIX                                        = "msg#";
   private static final String           ACK_PREFIX                                        = "ack#";
   private static final String           BACKUP_MSG_PREFIX                                 = "b_m#";
   private static final String           BACKUP_ACK_PREFIX                                 = "b_a#";

   private static final Logger           logger                                               = LoggerFactory
                                                                                                 .getLogger(DefaultMongoManager.class);

   private static final String           MONGO_CONFIG_FILENAME                             = "swallow-mongo.properties";
   private static final String           LION_CONFIG_FILENAME                              = "swallow-mongo-lion.properties";
   private static final String           DEFAULT_COLLECTION_NAME                           = "c";
   private static final String           TOPICNAME_DEFAULT                                 = "default";

   private static final String           LION_KEY_MSG_CAPPED_COLLECTION_SIZE               = "swallow.mongo.msgCappedCollectionSize";
   private static final String           LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM        = "swallow.mongo.msgCappedCollectionMaxDocNum";
   
   private static final String           LION_KEY_HEARTBEAT_SERVER_URI                     = "swallow.mongo.heartbeatServerURI";
   
   private static final int 			DEFAULT_CAPPED_COLLECTION_SIZE = 10;
   private static final int				DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM = 1;

   private static final int 			DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE = 50;
   private static final int				DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM = 1;


   private static final long             MILLION                                           = 1000000;

   //serverURI的名字可配置(consumer和producer在Lion上的名字是不同的)
   private final String                  severURILionKey;

   /** 缓存default collection 存在的标识，避免db.collectionExists的调用 */
   private final Map<DB, Byte>           collectionExistsSign                              = new ConcurrentHashMap<DB, Byte>();

   //lion config
   private volatile Map<String, Integer> msgTopicNameToSizes;
   private volatile Map<String, Integer> msgTopicNameToMaxDocNums;
   private volatile Map<String, Integer> ackTopicNameToSizes;
   private volatile Map<String, Integer> ackTopicNameToMaxDocNums;

   private volatile Map<String, Integer> backupMsgTopicNameToSizes;
   private volatile Map<String, Integer> backupMsgTopicNameToMaxDocNums;
   private volatile Map<String, Integer> backupAckTopicNameToSizes;
   private volatile Map<String, Integer> backupAckTopicNameToMaxDocNums;

   private volatile Mongo                heartbeatMongo;
   private volatile int                  heartbeatCappedCollectionSize;
   private volatile int                  heartbeatCappedCollectionMaxDocNum;
   private volatile Map<String, Mongo>   topicNameToMongoMap;

   //local config
   private MongoClientOptions            mongoOptions;

   private DynamicConfig                 dynamicConfig;

   private boolean messageCollectionCapped = true;
   
   /**
    * 从 Lion(配置topicName,serverUrl的列表) 和 MongoConfigManager(配置Mongo参数) 获取配置，创建
    * “topicName -&gt; Mongo实例” 的Map映射。<br>
    * <br>
    * 当 Lion 配置发现变化时，“topicName -&gt; Mongo实例” 的Map映射;<br>
    * 将 MongoClient 实例注入到DAO：dao通过调用MongoClient.getXXCollectiond得到Collection。
    * 
    * @param uri
    * @param config
    */
   public DefaultMongoManager(String severURILionKey, DynamicConfig dynamicConfig) {
      this.severURILionKey = severURILionKey;
      if (logger.isDebugEnabled()) {
         logger.debug("Init MongoClient - start.");
      }
      //读取properties配置(如果存在configFile，则使用configFile)
      MongoConfig config = new MongoConfig(MONGO_CONFIG_FILENAME);
      mongoOptions = config.buildMongoOptions();
      logger.info("MongoOptions=" + mongoOptions.toString());
      if (dynamicConfig != null) {
         this.dynamicConfig = dynamicConfig;
      } else {
         this.dynamicConfig = new LionDynamicConfig(LION_CONFIG_FILENAME);
      }
      loadLionConfig();
      if (logger.isDebugEnabled()) {
         logger.debug("Init MongoClient - done.");
      }
   }

   public DefaultMongoManager(String severURILionKey) {
      this(severURILionKey, null);
   }

   private void loadLionConfig() {
      try {
         //serverURI
         this.topicNameToMongoMap = parseURIAndCreateTopicMongo(dynamicConfig.get(this.severURILionKey).trim());
         //msgTopicNameToSizes
         String msgTopicNameToSizes = dynamicConfig.get(LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
         if (msgTopicNameToSizes != null) {
            this.msgTopicNameToSizes = parseSizeOrDocNum(msgTopicNameToSizes.trim());
         }
         //msgTopicNameToMaxDocNums(可选)
         String msgTopicNameToMaxDocNums = dynamicConfig.get(LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
         if (msgTopicNameToMaxDocNums != null) {
            this.msgTopicNameToMaxDocNums = parseSizeOrDocNum(msgTopicNameToMaxDocNums.trim());
         }
         //heartbeat
         this.heartbeatMongo = parseURIAndCreateHeartbeatMongo(dynamicConfig.get(LION_KEY_HEARTBEAT_SERVER_URI).trim());
         //添加Lion监听
         dynamicConfig.addConfigChangeListener(this);
      } catch (Exception e) {
         throw new IllegalArgumentException("Error Loading Config from Lion : " + e.getMessage(), e);
      }
   }

   /**
    * 解析URI，且创建heartbeat使用的Mongo实例
    */
   private Mongo parseURIAndCreateHeartbeatMongo(String serverURI) {
      Mongo mongo = null;
      List<ServerAddress> replicaSetSeeds = MongoUtils.parseUriToAddressList(serverURI);
      mongo = getExistsMongo(replicaSetSeeds);
      if (mongo == null) {
         mongo = new MongoClient(replicaSetSeeds, mongoOptions);
      }
      if (logger.isInfoEnabled()) {
         logger.info("parseURIAndCreateHeartbeatMongo() - parse " + serverURI + " to: " + mongo);
      }
      return mongo;
   }

   /**
    * 解析URI，且创建topic(msg和ack)使用的Mongo实例
    */
   public Map<String, Mongo> parseURIAndCreateTopicMongo(String serverURI) {
      try {
         //解析uri
    	  Map<String, List<String>> serverURIToTopicNames = parseServerURIString(serverURI);
         //根据uri创建Mongo，放到Map
         HashMap<String, Mongo> topicNameToMongoMap = new HashMap<String, Mongo>();
         for (Map.Entry<String, List<String>> entry : serverURIToTopicNames.entrySet()) {
            String uri = entry.getKey();
            List<ServerAddress> replicaSetSeeds = MongoUtils.parseUriToAddressList(uri);
            Mongo mongo = null;
            List<String> topicNames = entry.getValue();
            mongo = getExistsMongo(replicaSetSeeds);
            if (mongo == null) {//创建mongo实例
               mongo = new MongoClient(replicaSetSeeds, mongoOptions);
            }
            for (String topicName : topicNames) {
               topicNameToMongoMap.put(topicName, mongo);
            }
         }
         if (logger.isInfoEnabled()) {
            logger.info("parseURIAndCreateTopicMongo() - parse " + serverURI + " to: " + topicNameToMongoMap);
         }
         return topicNameToMongoMap;
      } catch (RuntimeException e) {
         throw new IllegalArgumentException(
               "Error parsing the '*ServerURI' property, the format is '<topicName>,default=<mongoURI>;<topicName>=<mongoURI>': "
                     + e.getMessage(), e);
      }
   }

	protected Map<String, List<String>> parseServerURIString(String serverURI) {
		
	    Map<String, List<String>> serverURIToTopicNames = new HashMap<String, List<String>>();
	    boolean defaultExists = false;
	    for (String topicNamesToURI : serverURI.split(";\\s*")) {
	       String[] splits = topicNamesToURI.split("=");
	       String mongoURI = splits[1].trim();
	       String topicNameStr = splits[0].trim();
	       List<String> topicNames = new ArrayList<String>();
	       for (String topicName : topicNameStr.split(",")) {
	    	   topicName = topicName.trim();
	          if (TOPICNAME_DEFAULT.equals(topicName)) {
	             defaultExists = true;
	          }
	          topicNames.add(topicName);
	       }
	       List<String> topicNames0 = serverURIToTopicNames.get(mongoURI);
	       if (topicNames0 != null) {
	          topicNames.addAll(topicNames0);
	       }
	       serverURIToTopicNames.put(mongoURI, topicNames);
	    }
	    //验证uri(default是必须存在的topicName)
	    if (!defaultExists) {
	       throw new IllegalArgumentException("The '" + this.severURILionKey
	             + "' property must contain 'default' topicName!");
	    }
	    return serverURIToTopicNames;
	}
	
/**
    * 如果已有的map或heartbeatMongo中已经存在相同的地址的Mongo实例，则重复使用
    */
   private Mongo getExistsMongo(List<ServerAddress> replicaSetSeeds) {
      Mongo mongo = null;
      if (this.topicNameToMongoMap != null) {//如果已有的map中已经存在该Mongo实例，则重复使用
         for (Mongo m : this.topicNameToMongoMap.values()) {
            if (equalsOutOfOrder(m.getAllAddress(), replicaSetSeeds)) {
               mongo = m;
               break;
            }
         }
      }
      if (this.heartbeatMongo != null) {//如果已经存在该Mongo实例，则重复使用
         if (this.equalsOutOfOrder(this.heartbeatMongo.getAllAddress(), replicaSetSeeds)) {
            mongo = this.heartbeatMongo;
         }
      }
      if (mongo != null) {
         if (logger.isInfoEnabled()) {
            logger.info("getExistsMongo() return a exists Mongo instance : " + mongo);
         }
      }
      return mongo;
   }

   private Map<String, Integer> parseSizeOrDocNum(String sizeStr) {
      try {
         Map<String, Integer> topicNameToSizes = new HashMap<String, Integer>();
         boolean defaultExists = false;
         for (String topicNameToSize : sizeStr.split(";")) {
            String[] splits = topicNameToSize.split("=");
            String size = splits[1];
            String topicNameStr = splits[0];
            for (String topicName : topicNameStr.split(",")) {
               if (TOPICNAME_DEFAULT.equals(topicName)) {
                  defaultExists = true;
               }
               int intSize = Integer.parseInt(size);
               if (intSize <= 0) {
                  throw new IllegalArgumentException("Size or DocNum value must larger than 0 :" + sizeStr);
               }
               topicNameToSizes.put(topicName, intSize);
            }
         }
         //验证uri(default是必须存在的topicName)
         if (!defaultExists) {
            throw new IllegalArgumentException("The '" + this.severURILionKey
                  + "' property must contain 'default' topicName!");
         }
         if (logger.isInfoEnabled()) {
            logger.info("parseSizeOrDocNum() - parse " + sizeStr + " to: " + topicNameToSizes);
         }
         return topicNameToSizes;
      } catch (Exception e) {
         throw new IllegalArgumentException(
               "Error parsing the '*Size' or '*MaxDocNum' property, the format is like 'default=<int>;<topicName>,<topicName>=<int>': "
                     + e.getMessage(), e);
      }
   }

   /**
    * 响应Lion更新事件时:<br>
    * (1)若是URI变化，重新构造Mongo实例，替换现有的Map值；<br>
    * (2)若是size和docnum配置项变化，则仅更新变量本身， 即只后续的创建Collection操作有影响。<br>
    * <p>
    * 该方法保证：<br>
    * (1)当新的Lion配置值有异常时，不会改变现有的值；<br>
    * (2)当新的Lion配置值正确，在正常更新值后，能有效替换现有的Map和int值
    * </p>
    */
   @Override
   public synchronized void onConfigChange(String key, String value) {
      if (logger.isInfoEnabled()) {
         logger.info("onChange() called.");
      }
      value = value.trim();
      try {
         if (this.severURILionKey.equals(key)) {
            Map<String, Mongo> oldTopicNameToMongoMap = this.topicNameToMongoMap;
            this.topicNameToMongoMap = parseURIAndCreateTopicMongo(value);
            Thread.sleep(5000);//DAO可能正在使用旧的Mongo，故等候5秒，才执行关闭操作
            closeUnuseMongo(oldTopicNameToMongoMap.values(), this.topicNameToMongoMap.values(), this.heartbeatMongo);
         } else if (LION_KEY_MSG_CAPPED_COLLECTION_SIZE.equals(key)) {
            this.msgTopicNameToSizes = parseSizeOrDocNum(value);
         } else if (LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM.equals(key)) {
            this.msgTopicNameToMaxDocNums = parseSizeOrDocNum(value);
         } else if (LION_KEY_HEARTBEAT_SERVER_URI.equals(key)) {
            Mongo oldMongo = this.heartbeatMongo;
            this.heartbeatMongo = parseURIAndCreateHeartbeatMongo(value);
            Thread.sleep(5000);//DAO可能正在使用旧的Mongo，故等候5秒，才执行关闭操作
            closeUnuseMongo(oldMongo, this.topicNameToMongoMap.values(), this.heartbeatMongo);
         }
      } catch (Exception e) {
         logger.error("Error occour when reset config from Lion, no config property would changed :" + e.getMessage(), e);
      }
   }

   private void closeUnuseMongo(Collection<Mongo> oldMongos, Collection<Mongo> curMongos, Mongo curMongo) {
      oldMongos.removeAll(curMongos);
      oldMongos.remove(curMongo);
      for (Mongo unuseMongo : oldMongos) {
         if (unuseMongo != null) {
            unuseMongo.close();
            if(logger.isInfoEnabled()){
            	logger.info("Close unuse Mongo: " + unuseMongo);
            }
         }
      }
   }

   private void closeUnuseMongo(Mongo oldMongo, Collection<Mongo> curMongos, Mongo curMongo) {
      if (!curMongos.contains(oldMongo) && oldMongo != curMongo) {
         oldMongo.close();
         if(logger.isInfoEnabled()){
        	 logger.info("Close unuse Mongo: " + oldMongo);
         }
      }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private boolean equalsOutOfOrder(List list1, List list2) {
      if (list1 == null || list2 == null) {
         return false;
      }
      return list1.containsAll(list2) && list2.containsAll(list1);
   }

   public DBCollection getMessageCollection(String topicName) {
      return getMessageCollection(topicName, null);
   }

   /**
    * @param topicName
    * @param consumerId consumerId为null表示使用非backup消息队列
    * @return
    */
   public DBCollection getMessageCollection(String topicName, String consumerId) {
	   
      Mongo mongo = getMongo(topicName);
      List<DBObject> index = new LinkedList<DBObject>();
      int size = -1;
      int max =  0;
      String dbName = getMessageDbName(topicName, consumerId);
      
      index.add(new BasicDBObject(MessageDAOImpl.ID, -1));
      if (consumerId == null) {
    	  if(messageCollectionCapped){
    		  size = getIntSafely(msgTopicNameToSizes, topicName);
    		  max = getIntSafely(msgTopicNameToMaxDocNums, topicName);
    	  }
      } else {
    	  index.add(new BasicDBObject(MessageDAOImpl.ORIGINAL_ID, -1));
         if(messageCollectionCapped){
        	 size = DEFAULT_BACKUP_CAPPED_COLLECTION_SIZE;
        	 max  = DEFAULT_BACKUP_CAPPED_COLLECTION_MAX_DOC_NUM;
         }
      }
      return getCollection(mongo, size, max, dbName, index);
   }

	private String getMessageDbName(String topicName, String consumerId) {

		if(consumerId == null){
			return MSG_PREFIX + topicName;
		}
		return BACKUP_MSG_PREFIX + topicName + "#" + consumerId;
	}

	@Override
	public void cleanMessageCollection(String topicName, String consumerId) {

		DBCollection collection = getMessageCollection(topicName, consumerId);
		
		boolean isCapped = collection.isCapped();

		//此处不能只dropCollection，如果只dropCollection会导致db大小不停增加
		dropDatabase(topicName, consumerId);
		
		markCollectionNotExists(getMongo(topicName).getDB(getMessageDbName(topicName, consumerId)));
		
		collection = getMessageCollection(topicName, consumerId);
		
		if(isCapped != collection.isCapped()){
			logger.warn("[cleanMessageCollection][capped type change]" + isCapped + "," + collection.isCapped());
		}
				
	}

	private void dropDatabase(String topicName, String consumerId) {
		
		String dbName = getMessageDbName(topicName, consumerId);
		Mongo mongo = getMongo(topicName);
		mongo.dropDatabase(dbName);
	}

private Mongo getMongo(String topicName) {
      Mongo mongo = this.topicNameToMongoMap.get(topicName);
      if (mongo == null) {
         if (logger.isDebugEnabled()) {
            logger.debug("topicname '" + topicName + "' do not match any Mongo Server, use default.");
         }
         mongo = this.topicNameToMongoMap.get(TOPICNAME_DEFAULT);
      }
      return mongo;
   }

   private int getIntSafely(Map<String, Integer> map, String key) {
      Integer i = null;
      if (map != null) {
         i = map.get(key);
         if (i == null) {
            i = map.get(TOPICNAME_DEFAULT);
         }
      }
      return i == null ? -1 : i.intValue();
   }

   public DBCollection getAckCollection(String topicName, String consumerId) {
      return getAckCollection(topicName, consumerId, false);
   }

   public DBCollection getAckCollection(String topicName, String consumerId, boolean isBackup) {
	   
      Mongo mongo = getMongo(topicName);
      String dbName = null;
      DBObject index = new BasicDBObject(AckDAOImpl.MSG_ID, -1);
      
      if (!isBackup) {
    	  dbName = ACK_PREFIX + topicName + "#" + consumerId;
      } else {
    	  dbName = BACKUP_ACK_PREFIX + topicName + "#" + consumerId;
      }
      return getCollection(mongo, DEFAULT_CAPPED_COLLECTION_SIZE, DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM
    		  , dbName, index);
   }

   public DBCollection getHeartbeatCollection(String ip) {

	   Mongo mongo = this.heartbeatMongo;
      return this.getCollection(mongo, DEFAULT_CAPPED_COLLECTION_SIZE, DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM,
            "heartbeat#" + ip, new BasicDBObject(HeartbeatDAOImpl.TICK, -1));
   }

   private DBCollection getCollection(Mongo mongo, int size, int cappedCollectionMaxDocNum, String dbName,
           DBObject ...indexDBObjects) {
	   List<DBObject> index = new LinkedList<DBObject>();
	   for(DBObject dbObject : indexDBObjects){
		   index.add(dbObject);
	   }
	   return getCollection(mongo, size, cappedCollectionMaxDocNum, dbName, index);
   }

   private DBCollection getCollection(Mongo mongo, int size, int cappedCollectionMaxDocNum, String dbName,
                                      List<DBObject> indexDBObjects) {
      //根据topicname从Mongo实例从获取DB
      DB db = mongo.getDB(dbName);
      
      //从DB实例获取Collection(因为只有一个Collection，所以名字均叫做c),如果不存在，则创建)
      DBCollection collection = null;
      if (!collectionExists(db)) {//从缓存检查default collection 存在的标识，避免db.collectionExists的调用
         synchronized (db) {
            if (!collectionExists(db) && !db.collectionExists(DEFAULT_COLLECTION_NAME)) {
               collection = createColletcion(db, DEFAULT_COLLECTION_NAME, size, cappedCollectionMaxDocNum,
                     indexDBObjects);
            }
            markCollectionExists(db);//缓存default collection 存在的标识，避免db.collectionExists的调用
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

   private DBCollection createColletcion(DB db, String collectionName, int size, int cappedCollectionMaxDocNum,
                                         List<DBObject> indexDBObjects) {

	   if(logger.isInfoEnabled()){
		   logger.info("[createColletcion]" + "name:" + collectionName + ", size:" + size + ", cappedCollectionMaxDocNum:" + cappedCollectionMaxDocNum);
	   }
	   
      DBObject options = new BasicDBObject();
      if (size > 0) {
	      options.put("capped", true);
	      options.put("size", size * MILLION);
	      if (cappedCollectionMaxDocNum > 0) {
	         options.put("max", cappedCollectionMaxDocNum * MILLION);//max row count
	      }
      }
      try {
         DBCollection collection = db.createCollection(collectionName, options);
         if (indexDBObjects != null) {
            for (DBObject indexDBObject : indexDBObjects) {
               collection.createIndex(indexDBObject);
               if(logger.isInfoEnabled()){
            	   logger.info("Ensure index " + indexDBObject + " on colleciton " + collection);
               }
            }
         }
         return collection;
      } catch (MongoException e) {
         if (e.getMessage() != null && e.getMessage().indexOf("collection already exists") >= 0) {
            //collection already exists
            logger.warn(e.getMessage() + ":the collectionName is " + collectionName);
            return db.getCollection(collectionName);
         } else {
            //other exception, can not connect to mongo etc, should abort
            throw e;
         }
      }
   }

   public void setDynamicConfig(DynamicConfig dynamicConfig) {
      this.dynamicConfig = dynamicConfig;
   }

   /**
    * 用于Hawk监控
    */
   public static class HawkMBean {

      private final WeakReference<DefaultMongoManager> mongoClient;

      private HawkMBean(DefaultMongoManager mongoClient) {
         this.mongoClient = new WeakReference<DefaultMongoManager>(mongoClient);
      }

      public String getSeverURILionKey() {
         return (mongoClient.get() != null) ? mongoClient.get().severURILionKey : null;
      }

      public Map<String, Integer> getMsgTopicNameToSizes() {
         return (mongoClient.get() != null) ? mongoClient.get().msgTopicNameToSizes : null;
      }

      public Map<String, Integer> getMsgTopicNameToMaxDocNums() {
         return (mongoClient.get() != null) ? mongoClient.get().msgTopicNameToMaxDocNums : null;
      }

      public Map<String, Integer> getAckTopicNameToSizes() {
         return (mongoClient.get() != null) ? mongoClient.get().ackTopicNameToSizes : null;
      }

      public Map<String, Integer> getAckTopicNameToMaxDocNums() {
         return (mongoClient.get() != null) ? mongoClient.get().ackTopicNameToMaxDocNums : null;
      }

      public Map<String, Integer> getBackupMsgTopicNameToSizes() {
         return (mongoClient.get() != null) ? mongoClient.get().backupMsgTopicNameToSizes : null;
      }

      public Map<String, Integer> getBackupMsgTopicNameToMaxDocNums() {
         return (mongoClient.get() != null) ? mongoClient.get().backupMsgTopicNameToMaxDocNums : null;
      }

      public Map<String, Integer> getBackupAckTopicNameToSizes() {
         return (mongoClient.get() != null) ? mongoClient.get().backupAckTopicNameToSizes : null;
      }

      public Map<String, Integer> getBackupAckTopicNameToMaxDocNums() {
         return (mongoClient.get() != null) ? mongoClient.get().backupAckTopicNameToMaxDocNums : null;
      }

      public String getHeartbeatMongo() {
         return (mongoClient.get() != null) ? mongoClient.get().heartbeatMongo.toString() : null;
      }

      public int getHeartbeatCappedCollectionSize() {
         return (mongoClient.get() != null) ? mongoClient.get().heartbeatCappedCollectionSize : null;
      }

      public int getHeartbeatCappedCollectionMaxDocNum() {
         return (mongoClient.get() != null) ? mongoClient.get().heartbeatCappedCollectionMaxDocNum : null;
      }

      public String getTopicNameToMongoMap() {
         return (mongoClient.get() != null) ? mongoClient.get().topicNameToMongoMap.toString() : null;
      }

      public String getMongoOptions() {
         return (mongoClient.get() != null) ? mongoClient.get().mongoOptions.toString() : null;
      }

      public String getCollectionExistsSign() {
         return (mongoClient.get() != null) ? mongoClient.get().collectionExistsSign.toString() : null;
      }

   }

   public static void main(String[] args) {
      int size = 10000;
      System.out.println((long) MILLION * size);
   }


}
