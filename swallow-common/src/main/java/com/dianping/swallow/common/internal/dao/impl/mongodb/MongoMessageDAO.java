package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.BSONTimestamp;

import com.dianping.swallow.common.internal.dao.impl.ReturnMessageWrapper;
import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

public class MongoMessageDAO extends AbstractMongoMessageDao {

	private static final long serialVersionUID = 1L;

	public static final String ID = "_id";
	public static final String ORIGINAL_ID = "o_id";
	public static final String CONTENT = "c";
	public static final String VERSION = "v";
	public static final String SHA1 = "s";
	public static final String GENERATED_TIME = "gt";
	public static final String PROPERTIES = "p";
	public static final String INTERNAL_PROPERTIES = "_p";
	public static final String TYPE = "t";
	public static final String SOURCE_IP = "si";
	
	
	public static final String  SRC_CONSUMER_IP = "cip";
	public static final String  TICK            = "t";

	
	public MongoMessageDAO(MongoCluster mongoCluster) {
		super(mongoCluster);

	}

	@Override
	public SwallowMessage getMessage(String topicName, Long messageId) {
		DBCollection collection = cluster.getMessageCollection(topicName);

		DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(messageId)).get();
		DBObject result = collection.findOne(query);
		if (result != null) {
			SwallowMessage swallowMessage = new SwallowMessage();
			try {
				convert(result, swallowMessage);
				return swallowMessage;
			} catch (RuntimeException e) {
				logger.error("Error when convert resultset to SwallowMessage.", e);
			}
		}
		return null;
	}

	@Override
	public Long getMaxMessageId(String topicName, String consumerId) {
		DBCollection collection = getCollection(topicName, consumerId);
		return getMaxMessageId(collection);
	}

	private DBCollection getCollection(String topicName, String consumerId) {
		return cluster.getMessageCollection(topicName, consumerId);
	}

	@Override
	public SwallowMessage getMaxMessage(String topicName) {
		DBCollection collection = cluster.getMessageCollection(topicName);

		DBObject orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(-1)).get();
		DBCursor cursor = collection.find().sort(orderBy).limit(1);
		try {
			if (cursor.hasNext()) {
				DBObject result = cursor.next();
				SwallowMessage swallowMessage = new SwallowMessage();
				try {
					convert(result, swallowMessage);
					return swallowMessage;
				} catch (RuntimeException e) {
					logger.error("[getMaxMessage][Error when convert resultset to SwallowMessage]" + result, e);
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

	@Override
	public ReturnMessageWrapper getMessagesGreaterThan(String topicName, String consumerId, Long messageId, int size) {
		DBCollection collection = getCollection(topicName, consumerId);
		
		List<SwallowMessage> list = getMessageGreaterThan(messageId, size, collection);
		int rawMessageSize = list.size();
		Long maxMessageId = rawMessageSize > 0? list.get(rawMessageSize -1).getMessageId() : -1; 
		
		return new ReturnMessageWrapper(list, rawMessageSize, maxMessageId);
	}

	@Override
	public void cleanMessage(String topicName, String consumerId) {
		if (logger.isInfoEnabled()) {
			logger.info("[cleanMessage][topic, consumerId]" + topicName + "," + consumerId);
		}
		cluster.cleanMessageCollection(topicName, consumerId);
	}

	private List<SwallowMessage> getMessageGreaterThan(Long messageId, int size, DBCollection collection) {
		DBObject gt = BasicDBObjectBuilder.start().add("$gt", MongoUtils.longToBSONTimestamp(messageId)).get();
		DBObject query = BasicDBObjectBuilder.start().add(ID, gt).get();
		DBObject orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(1)).get();
		DBCursor cursor = collection.find(query).sort(orderBy).limit(size);

		List<SwallowMessage> list = new ArrayList<SwallowMessage>();
		try {
			while (cursor.hasNext()) {
				DBObject result = cursor.next();
				SwallowMessage swallowMessage = new SwallowMessage();
				try {
					convert(result, swallowMessage);
					list.add(swallowMessage);
				} catch (RuntimeException e) {
					logger.error("Error when convert resultset to SwallowMessage.", e);
				}
			}
		} finally {
			cursor.close();
		}
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	private void convert(DBObject result, SwallowMessage swallowMessage) {
		
		BSONTimestamp timestamp = (BSONTimestamp) result.get(ID);
		swallowMessage.setMessageId(MongoUtils.BSONTimestampToLong(timestamp));

		BSONTimestamp originalTimestamp = (BSONTimestamp) result.get(ORIGINAL_ID);
		if (originalTimestamp != null) {
			swallowMessage.setBackupMessageId(MongoUtils.BSONTimestampToLong(originalTimestamp));
		}

		swallowMessage.setContent(result.get(CONTENT));// content
		swallowMessage.setVersion((String) result.get(VERSION));// version
		swallowMessage.setGeneratedTime((Date) result.get(GENERATED_TIME));// generatedTime
		Map<String, String> propertiesBasicDBObject = (Map<String, String>) result.get(PROPERTIES);// mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
		if (propertiesBasicDBObject != null) {
			HashMap<String, String> properties = new HashMap<String, String>(propertiesBasicDBObject);
			swallowMessage.setProperties(properties);// properties
		}
		Map<String, String> internalPropertiesBasicDBObject = (Map<String, String>) result.get(INTERNAL_PROPERTIES);// mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
		if (internalPropertiesBasicDBObject != null) {
			swallowMessage.putInternalProperties(internalPropertiesBasicDBObject);// properties
		}
		swallowMessage.setSha1((String) result.get(SHA1));// sha1
		swallowMessage.setType((String) result.get(TYPE));// type
		swallowMessage.setSourceIp((String) result.get(SOURCE_IP));// sourceIp
	}

	public void saveMessage(String topicName, List<SwallowMessage> messages) {
		saveMessage(topicName, null, messages);
	}

	private void saveMessage(String topicName, String consumerId, List<SwallowMessage> messages) {

		final DBCollection collection = getCollection(topicName, consumerId);

		final List<DBObject> mongoMessages = new ArrayList<DBObject>();
		
		for(SwallowMessage message : messages){
			mongoMessages.add(createMongoMessage(topicName, consumerId, message));
		}

		doAndCheckResult(new MongoAction() {
			@Override
			public WriteResult doAction() {
				
				return collection.insert(mongoMessages);
			}
		});
	}

	
	@Override
	protected void doSaveMessage(String topicName, String consumerId, SwallowMessage message) {

		final DBCollection collection = getCollection(topicName, consumerId);

		final DBObject mongoMessage = createMongoMessage(topicName, consumerId, message);

		doAndCheckResult(new MongoAction() {
			@Override
			public WriteResult doAction() {
				return collection.insert(mongoMessage);
			}
		});
	}

	private DBObject createMongoMessage(String topicName, String consumerId, SwallowMessage message) {
		
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().add(ID, new BSONTimestamp());
		
		Long backupMessageId = message.getBackupMessageId();
		if (backupMessageId != null) {
			builder.add(ORIGINAL_ID, MongoUtils.longToBSONTimestamp(backupMessageId));
		}
		// content
		String content = message.getContent();
		if (content != null && !"".equals(content.trim())) {
			builder.add(CONTENT, content);
		}
		// generatedTime
		Date generatedTime = message.getGeneratedTime();
		if (generatedTime != null) {
			builder.add(GENERATED_TIME, generatedTime);
		}
		// version
		String version = message.getVersion();
		if (version != null && !"".equals(version.trim())) {
			builder.add(VERSION, version);
		}
		// properties
		Map<String, String> properties = message.getProperties();
		if (properties != null && properties.size() > 0) {
			builder.add(PROPERTIES, properties);
		}
		
		// internalProperties
		Map<String, String> internalProperties = message.getInternalProperties();
		if(internalProperties != null && internalProperties.size() >0){
			builder.add(INTERNAL_PROPERTIES, internalProperties);
		}
		// sha1
		String sha1 = message.getSha1();
		if (sha1 != null && !"".equals(sha1.trim())) {
			builder.add(SHA1, sha1);
		}
		// type
		String type = message.getType();
		if (type != null && !"".equals(type.trim())) {
			builder.add(TYPE, type);
		}
		// sourceIp
		String sourceIp = message.getSourceIp();
		if (sourceIp != null && !"".equals(sourceIp.trim())) {
			builder.add(SOURCE_IP, sourceIp);
		}
		
		return builder.get();
	}

	@Override
	public void retransmitMessage(String topicName, SwallowMessage message) {
		DBCollection collection = getCollection(topicName, null);

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().add(ID, new BSONTimestamp());

		// content
		String content = message.getContent();
		if (content != null && !"".equals(content.trim())) {
			builder.add(CONTENT, content);
		}
		// generatedTime
		Date generatedTime = message.getGeneratedTime();
		if (generatedTime != null) {
			builder.add(GENERATED_TIME, generatedTime);
		}
		// version
		String version = message.getVersion();
		if (version != null && !"".equals(version.trim())) {
			builder.add(VERSION, version);
		}
		// properties
		Map<String, String> properties = message.getProperties();
		if (properties != null && properties.size() > 0) {
			builder.add(PROPERTIES, properties);
		}
		// internalProperties
		message.putInternalProperty(InternalProperties.RETRANSMIT, message.getMessageId().toString());
		addDefaultInternalProperties(message);
		
		builder.add(INTERNAL_PROPERTIES, message.getInternalProperties());
		// sha1
		String sha1 = message.getSha1();
		if (sha1 != null && !"".equals(sha1.trim())) {
			builder.add(SHA1, sha1);
		}
		// type
		String type = message.getType();
		if (type != null && !"".equals(type.trim())) {
			builder.add(TYPE, type);
		}
		// sourceIp
		String sourceIp = message.getSourceIp();
		if (sourceIp != null && !"".equals(sourceIp.trim())) {
			builder.add(SOURCE_IP, sourceIp);
		}

		collection.insert(builder.get());
	}

	@Override
	public int count(String topicName) {

		DBCollection collection = getCollection(topicName, null);
		return collection.find().size();
	}

	@Override
	public long getAccumulation(String topicName, String consumerId) {

		DBCollection collection = cluster.getAckCollection(topicName, consumerId);
		DBCursor cursor = collection.find().sort(new BasicDBObject(ID, -1)).limit(1);

		BSONTimestamp currentIndex = MongoUtils.getTimestampByCurTime();

		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			currentIndex = (BSONTimestamp) object.get(ID);
		}

		DBCollection msgCollection = getCollection(topicName, null);
		return msgCollection.count(new Query().gt(ID, currentIndex).build());
	}

	
	

	   @Override
	   public Long getAckMaxMessageId(String topicName, String consumerId, boolean isBackup) {
	      DBCollection collection = getAckCollection(topicName, consumerId, isBackup);
	      return getMaxMessageId(collection);
	   }

	   private DBCollection getAckCollection(String topicName, String consumerId, boolean isBackup) {
	      return cluster.getAckCollection(topicName, consumerId, isBackup);
	   }

	   private Long getMaxMessageId(DBCollection collection) {
		   
	      DBObject fields = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(1)).get();
	      DBObject orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(-1)).get();
	      DBCursor cursor = collection.find(new BasicDBObject(), fields).sort(orderBy).limit(1);
	      try {
	         if (cursor.hasNext()) {
	            DBObject result = cursor.next();
	            BSONTimestamp timestamp = (BSONTimestamp) result.get(ID);
	            return MongoUtils.BSONTimestampToLong(timestamp);
	         }
	      } finally {
	         cursor.close();
	      }
	      return null;
	   }

		@Override
		public void cleanAck(String topicName, String consumerId, boolean isBackup) {
			
			cluster.cleanAckCollection(topicName, consumerId, isBackup);;
		}


	   @Override
	   public void addAck(String topicName, String consumerId, Long messageId, String sourceConsumerIp, boolean isBackup) {
	      DBCollection collection = getAckCollection(topicName, consumerId, isBackup);
	      addAck(messageId, sourceConsumerIp, collection);
	   }

	   private void addAck(Long messageId, String sourceConsumerIp, DBCollection collection) {
	      BSONTimestamp timestamp = MongoUtils.longToBSONTimestamp(messageId);
	      if(logger.isDebugEnabled()){
	    	  logger.debug("[add][add ack id]" + timestamp);
	      }
	      Date curTime = new Date();
	      try {
	         DBObject add = BasicDBObjectBuilder.start().add(ID, timestamp).add(SRC_CONSUMER_IP, sourceConsumerIp)
	               .add(TICK, curTime).get();
	         collection.insert(add);
	      } catch (MongoException e) {
	         if (e.getMessage() != null && e.getMessage().indexOf("duplicate key") >= 0 || e.getCode() == 11000) {
	            //_id already exists
	            logger.warn(e.getMessage() + ": _id is " + timestamp);
	         } else {
	            throw e;
	         }
	      }
	   }

	@Override
	public Long getMessageEmptyAckId(String topicName) {

		return MongoUtils.getLongByCurTime();
	}


}
