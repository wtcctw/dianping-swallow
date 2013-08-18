package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MessageDAOImpl implements MessageDAO {

   private static final Logger LOG                 = LoggerFactory.getLogger(MessageDAOImpl.class);

   public static final String  ID                  = "_id";
   public static final String  ORIGINAL_ID         = "o_id";
   public static final String  CONTENT             = "c";
   public static final String  VERSION             = "v";
   public static final String  SHA1                = "s";
   public static final String  GENERATED_TIME      = "gt";
   public static final String  PROPERTIES          = "p";
   public static final String  INTERNAL_PROPERTIES = "_p";
   public static final String  TYPE                = "t";
   public static final String  SOURCE_IP           = "si";

   private MongoClient         mongoClient;

   public void setMongoClient(MongoClient mongoClient) {
      this.mongoClient = mongoClient;
   }

   @Override
   public SwallowMessage getMessage(String topicName, Long messageId) {
      DBCollection collection = this.mongoClient.getMessageCollection(topicName);

      DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(messageId)).get();
      DBObject result = collection.findOne(query);
      if (result != null) {
         SwallowMessage swallowMessage = new SwallowMessage();
         try {
            convert(result, swallowMessage);
            return swallowMessage;
         } catch (RuntimeException e) {
            LOG.error("Error when convert resultset to SwallowMessage.", e);
         }
      }
      return null;
   }

   @Override
   public Long getMaxMessageId(String topicName, String consumerId) {
      DBCollection collection = getCollection(topicName, consumerId);
      return getMaxMessageId(collection);
   }

   @Override
   public Long getMaxMessageId(String topicName) {
      return getMaxMessageId(topicName, null);
   }

   private DBCollection getCollection(String topicName, String consumerId) {
      return this.mongoClient.getMessageCollection(topicName, consumerId);
   }

   private Long getMaxMessageId(DBCollection collection) {
      DBObject fields = BasicDBObjectBuilder.start().add(ID, 1).get();
      DBObject orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(-1)).get();
      DBCursor cursor = collection.find(null, fields).sort(orderBy).limit(1);
      try {
         if (cursor.hasNext()) {
            BSONTimestamp timestamp = (BSONTimestamp) cursor.next().get(ID);
            return MongoUtils.BSONTimestampToLong(timestamp);
         }
      } finally {
         cursor.close();
      }
      return null;
   }

   @Override
   public SwallowMessage getMaxMessage(String topicName) {
      DBCollection collection = this.mongoClient.getMessageCollection(topicName);

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
               LOG.error("Error when convert resultset to SwallowMessage.", e);
            }
         }
      } finally {
         cursor.close();
      }
      return null;
   }

   @Override
   public List<SwallowMessage> getMessagesGreaterThan(String topicName, String consumerId, Long messageId, int size) {
      DBCollection collection = getCollection(topicName, consumerId);
      List<SwallowMessage> list = getMessageGreaterThan(messageId, size, collection);
      return list;
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
               LOG.error("Error when convert resultset to SwallowMessage.", e);
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
      BSONTimestamp originalTimestamp = (BSONTimestamp) result.get(ORIGINAL_ID);

      if (originalTimestamp != null) {
         swallowMessage.setMessageId(MongoUtils.BSONTimestampToLong(originalTimestamp));
         swallowMessage.setBackupMessageId(MongoUtils.BSONTimestampToLong(timestamp));
         swallowMessage.setBackup(true);
      } else {
         swallowMessage.setMessageId(MongoUtils.BSONTimestampToLong(timestamp));
         swallowMessage.setBackup(false);
      }

      swallowMessage.setContent(result.get(CONTENT));//content
      swallowMessage.setVersion((String) result.get(VERSION));//version
      swallowMessage.setGeneratedTime((Date) result.get(GENERATED_TIME));//generatedTime
      Map<String, String> propertiesBasicDBObject = (Map<String, String>) result.get(PROPERTIES);//mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
      if (propertiesBasicDBObject != null) {
         HashMap<String, String> properties = new HashMap<String, String>(propertiesBasicDBObject);
         swallowMessage.setProperties(properties);//properties
      }
      Map<String, String> internalPropertiesBasicDBObject = (Map<String, String>) result.get(INTERNAL_PROPERTIES);//mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
      if (internalPropertiesBasicDBObject != null) {
         HashMap<String, String> properties = new HashMap<String, String>(internalPropertiesBasicDBObject);
         swallowMessage.setInternalProperties(properties);//properties
      }
      swallowMessage.setSha1((String) result.get(SHA1));//sha1
      swallowMessage.setType((String) result.get(TYPE));//type
      swallowMessage.setSourceIp((String) result.get(SOURCE_IP));//sourceIp
   }

   @Override
   public void saveMessage(String topicName, SwallowMessage message) {
      saveMessage(topicName, null, message);
   }

   @Override
   public void saveMessage(String topicName, String consumerId, SwallowMessage message) {
      DBCollection collection = getCollection(topicName, consumerId);

      BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().add(ID, new BSONTimestamp());
      //如果有backupMessageId，则表示是备份消息，那么messageId则作为ORIGINAL_ID存起来
      if (consumerId != null) {
         builder.add(ORIGINAL_ID, MongoUtils.longToBSONTimestamp(message.getMessageId()));
      }
      //content
      String content = message.getContent();
      if (content != null && !"".equals(content.trim())) {
         builder.add(CONTENT, content);
      }
      //generatedTime
      Date generatedTime = message.getGeneratedTime();
      if (generatedTime != null) {
         builder.add(GENERATED_TIME, generatedTime);
      }
      //version
      String version = message.getVersion();
      if (version != null && !"".equals(version.trim())) {
         builder.add(VERSION, version);
      }
      //properties
      Map<String, String> properties = message.getProperties();
      if (properties != null && properties.size() > 0) {
         builder.add(PROPERTIES, properties);
      }
      //internalProperties
      Map<String, String> internalProperties = message.getInternalProperties();
      if (internalProperties != null && internalProperties.size() > 0) {
         builder.add(INTERNAL_PROPERTIES, internalProperties);
      }
      //sha1
      String sha1 = message.getSha1();
      if (sha1 != null && !"".equals(sha1.trim())) {
         builder.add(SHA1, sha1);
      }
      //type
      String type = message.getType();
      if (type != null && !"".equals(type.trim())) {
         builder.add(TYPE, type);
      }
      //sourceIp
      String sourceIp = message.getSourceIp();
      if (sourceIp != null && !"".equals(sourceIp.trim())) {
         builder.add(SOURCE_IP, sourceIp);
      }

      collection.insert(builder.get());
   }

}
