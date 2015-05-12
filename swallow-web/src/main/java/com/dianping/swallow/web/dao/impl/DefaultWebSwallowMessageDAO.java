package com.dianping.swallow.web.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.BSONTimestamp;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.dao.WebSwallowMessageDao;
import com.dianping.swallow.web.model.WebSwallowMessage;
import com.dianping.swallow.web.util.MongoUtils;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 * 2015年4月20日 下午9:31:41 
 */
public class DefaultWebSwallowMessageDao  implements WebSwallowMessageDao {
 
	private 			 WebMongoManager        webMongoManager;
    private static final String 				MESSAGE_COLLECTION                 = "c";
    private static final String 				TIMEFORMAT                         = "yyyy-MM-dd HH:mm:ss";
    private static final String 				ID                                 = "_id";
    private static final String 				OID                                = "o_id";
    private static final String 				GT                                 = "gt";
    private static final String 				SIZE                               = "size";
    private static final String 				MESSAGE                            = "message";
    private static final String 				SI                                 = "si"; 
    private static final String 				V                                  = "v";
    private static final String 				C                                  = "c";
    private static final String 				P                                  = "p"; 
    private static final String 				IP                                 = "_p";
    private static final String 				T                                  = "t";
    private static final String 				S                                  = "s";
    
    
    public void setWebMongoManager(DefaultWebMongoManager webMongoManager){
        
    	this.webMongoManager = webMongoManager;
    	
    }
     
    @Override
    public void create(WebSwallowMessage p, String topicName) {
        this.webMongoManager.getMessageMongoTemplate(topicName).insert(p, MESSAGE_COLLECTION);
    }
 
    @Override
    public WebSwallowMessage readById(long mid, String topicName) {
    	DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(MESSAGE_COLLECTION);
    	DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(mid)).get();
        DBObject result = collection.findOne(query);
        if (result != null) {
            WebSwallowMessage swallowMessage = new WebSwallowMessage();
            try {
               convert(result, swallowMessage);
               return swallowMessage;
            } catch (RuntimeException e) {
            }
         }
        return null;
    }
 
    @Override
    public void update(WebSwallowMessage p, String topicName) {
    	this.webMongoManager.getMessageMongoTemplate(topicName).save(p, MESSAGE_COLLECTION);
    }
 
    @Override
    public int deleteById(String id, String topicName) {
        Query query = new Query(Criteria.where(ID).is(id));
        WriteResult result = this.webMongoManager.getMessageMongoTemplate(topicName).remove(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
        return result.getN();
    }
    
    @Override
	public long count(String topicName){
    	Query query = new Query();
    	return this.webMongoManager.getMessageMongoTemplate(topicName).count(query, MESSAGE_COLLECTION);
    }
	
    @Override
    public Map<String, Object> findByTopicname(int offset, int limit, String topicName){
    	List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	Query query = new Query();
    	query.with(new Sort(new Sort.Order(Direction.DESC, ID)));  //降序
    	query.skip(offset).limit(limit);
        messageList = this.webMongoManager.getMessageMongoTemplate(topicName).find(query, WebSwallowMessage.class, MESSAGE_COLLECTION);

       	long size = this.count(topicName);
    	map.put(SIZE, size);
    	map.put(MESSAGE, messageList);
        return map;
    }
    
    @Override
    public List<WebSwallowMessage> findSpecific(int offset, int limit, long mid, String topicName){
    	Query query = new Query();
    	if(mid == 0){
    		query.with(new Sort(new Sort.Order(Direction.DESC, ID)));
    	}
    	else{
    		return findSpecificWithId(offset, limit, mid, topicName);
    	}
        query.skip(offset).limit(limit);
        return this.webMongoManager.getMessageMongoTemplate(topicName).find(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
    }
    
    @Override
    public Map<String, Object>  findByIp(int offset, int limit, String ip, String topicName){
    	List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	Query query1 = new Query(Criteria.where(SI).is(ip));
    	query1.skip(offset).limit(limit);
    	messageList = this.webMongoManager.getMessageMongoTemplate(topicName).find(query1, WebSwallowMessage.class, MESSAGE_COLLECTION);
    	Query query2 = new Query(Criteria.where(SI).is(ip));
       	long size = this.webMongoManager.getMessageMongoTemplate(topicName).count(query2, MESSAGE_COLLECTION);
    	map.put(SIZE, size);
    	map.put(MESSAGE, messageList);
        return map;
    }
    
    @Override
    public Map<String, Object> findByTime(int offset, int limit, String startdt, String stopdt, String topicName){
    	Map<String, Object> map = new HashMap<String, Object>();
    	DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(MESSAGE_COLLECTION);
    	SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
    	Date datestart = null;
		Date datestop = null;
		try {
			datestart = addEightHours(sdf.parse(startdt));
			datestop = addEightHours(sdf.parse(stopdt));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Long startlong = MongoUtils.getLongByDate(datestart);
		Long stoplong  = MongoUtils.getLongByDate(datestop);
		DBObject query = BasicDBObjectBuilder.start()
				.add(ID, BasicDBObjectBuilder.start()
					.add("$gt", MongoUtils.longToBSONTimestamp(startlong))
					.add("$lt", MongoUtils.longToBSONTimestamp(stoplong))
					.get())
				.get();
        DBObject orderBy = BasicDBObjectBuilder.start().add(ID, -1).get();
        DBCursor cursor = collection.find(query).skip(offset).sort(orderBy).limit(limit);
        DBCursor cursorall = collection.find(query);
        Long size =(long) cursorall.count();

        List<WebSwallowMessage> list = new ArrayList<WebSwallowMessage>();
        try {
           while (cursor.hasNext()) {
              DBObject result = cursor.next();
              
              WebSwallowMessage swallowMessage = new WebSwallowMessage();
              try {
                 convert(result, swallowMessage);
                 list.add(swallowMessage);
              } catch (RuntimeException e) {
            	  e.printStackTrace();
              }
           }
        } finally {
           cursor.close();
        }
    	map.put(SIZE, size);
    	map.put(MESSAGE, list);
        return map;
    }
    
    @Override
    public Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt, String topicName){
    	List<WebSwallowMessage> list = new ArrayList<WebSwallowMessage>();
    	Query query = new Query();
    	if(mid == 0){
    		query.with(new Sort(new Sort.Order(Direction.DESC, ID)));  //降序
    		query.skip(offset).limit(limit);
    		list = this.webMongoManager.getMessageMongoTemplate(topicName).find(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
    	}
    	else{
    		list = findSpecificWithId(offset, limit, mid, topicName);
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
    	Date datestart = null;
		Date datestop = null;
		try {
			datestart = addEightHours(sdf.parse(startdt));
			datestop = addEightHours(sdf.parse(stopdt));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Long startlong = MongoUtils.getLongByDate(datestart);
		Long stoplong  = MongoUtils.getLongByDate(datestop);
		for(int i = 0; i < list.size(); ++i){
			if(!(mid > startlong && mid < stoplong))
				list.remove(i);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Long size =(long) list.size();
    	map.put(SIZE, size);
    	map.put(MESSAGE, list);
        return map;

    }
    
    @SuppressWarnings({ "unchecked" })
    private void convert(DBObject result, WebSwallowMessage swallowMessage) {
       BSONTimestamp timestamp = (BSONTimestamp) result.get(ID);
       BSONTimestamp originalTimestamp = (BSONTimestamp) result.get(OID);
       if (originalTimestamp != null)
           swallowMessage.setO_id(originalTimestamp);  //modify origin code
       swallowMessage.set_id(timestamp);

       swallowMessage.setC((String)result.get(C));//content
       swallowMessage.setV((String) result.get(V));//version
       swallowMessage.setGt((Date) result.get(GT));//generatedTime
       Map<String, String> propertiesBasicDBObject = (Map<String, String>) result.get(P);//mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
       if (propertiesBasicDBObject != null) {
          HashMap<String, String> properties = new HashMap<String, String>(propertiesBasicDBObject);
          swallowMessage.setP(properties.toString());//properties
       }
       Map<String, String> internalPropertiesBasicDBObject = (Map<String, String>) result.get(IP);//mongo返回是一个BasicDBObject，转化成jdk的HashMap，以免某些序列化方案在反序列化需要依赖BasicDBObject
       if (internalPropertiesBasicDBObject != null) {
          HashMap<String, String> properties = new HashMap<String, String>(internalPropertiesBasicDBObject);
          swallowMessage.set_p(properties.toString());//properties
       }
       swallowMessage.setS((String) result.get(S));//sha1
       swallowMessage.setT((String) result.get(T));//type
       swallowMessage.setSi((String) result.get(SI));//sourceIp
    }
    

    private Date addEightHours(Date date){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.add(Calendar.MINUTE, 0 * 60);
    	return c.getTime();
    }
    
    private List<WebSwallowMessage> findSpecificWithId(int offset, int limit, long mid, String topicName){
    	DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(MESSAGE_COLLECTION);
    	DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(mid)).get();
        DBObject result = collection.findOne(query);
        List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
        if (result != null) {
            WebSwallowMessage swallowMessage = new WebSwallowMessage();
            try {
               convert(result, swallowMessage);
               messageList.add(swallowMessage);
            } catch (RuntimeException e) {
            }
         }
        return messageList;
    }

	@Override
	public MongoTemplate getMessageMongoTemplate(String topicName) {
		return this.webMongoManager.getMessageMongoTemplate(topicName);
	}

	@Override
	public List<MongoClient> getAllReadMongo() {
		return this.webMongoManager.getAllReadMongo();
	}

	@Override
	public Map<String, MongoClient> getTopicNameToMongoMap() {
		return this.webMongoManager.getTopicNameToMongoMap();
	}

	public WebMongoManager getWebwebMongoManager() {
		return this.webMongoManager;
	}

}