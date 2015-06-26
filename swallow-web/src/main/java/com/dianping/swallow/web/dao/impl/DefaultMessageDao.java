package com.dianping.swallow.web.dao.impl;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.model.Message;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年4月20日 下午9:31:41
 */
@Component
public class DefaultMessageDao extends AbstractDao implements MessageDao {

	@Autowired
	private WebMongoManager webMongoManager;
	private static final String MESSAGE_COLLECTION = "c";
	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String ID = "_id";
	private static final String OID = "o_id";
	private static final String GT = "gt";
	private static final String SIZE = "size";
	private static final String MESSAGE = "message";
	private static final String SI = "si";
	private static final String V = "v";
	private static final String C = "c";
	private static final String P = "p";
	private static final String IP = "_p";
	private static final String T = "t";
	private static final String S = "s";

	public void setWebMongoManager(DefaultWebMongoManager webMongoManager) {

		this.webMongoManager = webMongoManager;
	}
	
	@Override
	public boolean create(Message p, String topicName) {
		try {
			this.webMongoManager.getMessageMongoTemplate(topicName).insert(p, MESSAGE_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + p, e);
		}
		return false;
	}

	@Override
	public Message readById(long mid, String topicName) {
		DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(
				MESSAGE_COLLECTION);
		DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(mid)).get();
		DBObject result = collection.findOne(query);
		if (result != null) {
			Message swallowMessage = new Message();
			try {
				convert(result, swallowMessage);
				return swallowMessage;
			} catch (RuntimeException e) {
				logger.error("Error when convert resultset to SwallowMessage.", e);
			}
		}
		return (Message) result;
	}

	@Override
	public boolean update(Message p, String topicName) {
		try {
			this.webMongoManager.getMessageMongoTemplate(topicName).save(p, MESSAGE_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + p, e);
		}
		return false;
	}

	@Override
	public int deleteById(String id, String topicName) {
		Query query = new Query(Criteria.where(ID).is(id));
		WriteResult result = this.webMongoManager.getMessageMongoTemplate(topicName).remove(query, Message.class,
				MESSAGE_COLLECTION);
		return result.getN();
	}

	@Override
	public long count(String topicName) {
		Query query = new Query();
		return this.webMongoManager.getMessageMongoTemplate(topicName).count(query, MESSAGE_COLLECTION);
	}

	@Override
	public Map<String, Object> findByTopicname(int offset, int limit, String topicName, String baseMid, boolean sort) {
		List<Message> messageList = new ArrayList<Message>();
		Query query = new Query();
		if(!sort){
			query.with(new Sort(new Sort.Order(Direction.DESC, ID)));
		}else{
			query.with(new Sort(new Sort.Order(Direction.ASC, ID)));
		}
		if (StringUtils.isEmpty(baseMid)) {
			query.skip(offset).limit(limit);
			query.fields().exclude(C);
			messageList = this.webMongoManager.getMessageMongoTemplate(topicName).find(query, Message.class,
					MESSAGE_COLLECTION);

		} else {
			DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(
					MESSAGE_COLLECTION);
			Long mid = Long.parseLong(baseMid);
			if (mid > 0) {
				messageList = getMessageFromOneSide(mid, limit, collection, true, sort);
			} else {
				messageList = getMessageFromOneSide(-mid, limit, collection, false, sort);
			}
		}

		return getResponse(messageList, this.count(topicName));
	}

	@Override
	public Map<String, Object> findSpecific(int offset, int limit, long mid, String topicName, boolean sort) {
		if (mid == 0) {
			return findSpecificWithoutId(offset, limit, topicName, sort);
		} else
			return findSpecificWithId(offset, limit, mid, topicName);

	}

	private Map<String, Object> findSpecificWithoutId(int offset, int limit, String topicName, boolean sort) {
		Query query = new Query();
		if(!sort){
			query.with(new Sort(new Sort.Order(Direction.DESC, ID)));
		}else{
			query.with(new Sort(new Sort.Order(Direction.ASC, ID)));
		}
		query.skip(offset).limit(limit);
		if (limit - offset != 1) { // return C until it is necessary
			query.fields().exclude(C);
		}
		List<Message> messageList = this.webMongoManager.getMessageMongoTemplate(topicName).find(query, Message.class,
				MESSAGE_COLLECTION);

		return getResponse(messageList, this.count(topicName));
	}

	@Override
	public Map<String, Object> findByIp(int offset, int limit, String ip, String topicName) {
		List<Message> messageList = new ArrayList<Message>();
		Query query1 = new Query(Criteria.where(SI).is(ip));
		query1.skip(offset).limit(limit);
		query1.fields().exclude(C);
		query1.with(new Sort(new Sort.Order(Direction.DESC, ID)));
		messageList = this.webMongoManager.getMessageMongoTemplate(topicName).find(query1, Message.class,
				MESSAGE_COLLECTION);
		Query query2 = new Query(Criteria.where(SI).is(ip));
		long size = this.webMongoManager.getMessageMongoTemplate(topicName).count(query2, MESSAGE_COLLECTION);
		return getResponse(messageList, size);
	}

	@Override
	public Map<String, Object> findByTime(int offset, int limit, String startdt, String stopdt, String topicName,
			String baseMid, boolean sort) {

		List<Message> list = new ArrayList<Message>();
		DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(
				MESSAGE_COLLECTION);
		SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
		Long startlong = null;
		Long stoplong = null;
		try {
			startlong = MongoUtils.getLongByDate(sdf.parse(startdt));
			stoplong = MongoUtils.getLongByDate(sdf.parse(stopdt));
		} catch (ParseException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error when parse date to Long.", e);
			}
		}
		DBObject query = BasicDBObjectBuilder
				.start()
				.add(ID,
						BasicDBObjectBuilder.start().add("$gt", MongoUtils.longToBSONTimestamp(startlong))
								.add("$lt", MongoUtils.longToBSONTimestamp(stoplong)).get()).get();
		DBObject orderBy;
		if(!sort){
			orderBy = BasicDBObjectBuilder.start().add(ID, -1).get();
		}else{
			orderBy = BasicDBObjectBuilder.start().add(ID, 1).get();
		}
		DBCursor cursor = collection.find(query).skip(offset).sort(orderBy).limit(limit);
		DBCursor cursorall = collection.find(query);
		Long size = (long) cursorall.count();

		if (StringUtils.isNotEmpty(baseMid)) {
			long time = 0;
			if (baseMid.contains(":")) {
				try {
					time = MongoUtils.getLongByDate(sdf.parse(startdt));
				} catch (ParseException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error when parse date to Long.", e);
					}
				}
			} else {
				time = Long.parseLong(baseMid);
			}
			if (time < 0) {
				list = getMessageFromOneSide(-time, limit, collection, false, sort);
				return getResponse(list, size);
			} else {
				list = getMessageFromOneSide(time, limit, collection, true, sort);
				return getResponse(list, size);
			}
		}

		try {
			while (cursor.hasNext()) {
				DBObject result = cursor.next();

				Message swallowMessage = new Message();
				try {
					convert(result, swallowMessage);
					list.add(swallowMessage);
				} catch (RuntimeException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error when convert resultset to WebSwallowMessage.", e);
					}
				}
			}
		} finally {
			cursor.close();
		}
		return getResponse(list, size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt,
			String topicName) {
		List<Message> list = new ArrayList<Message>();
		Query query = new Query();
		if (mid == 0) {
			query.with(new Sort(new Sort.Order(Direction.DESC, ID)));
			query.skip(offset).limit(limit);
			list = this.webMongoManager.getMessageMongoTemplate(topicName).find(query, Message.class,
					MESSAGE_COLLECTION);
		} else {
			list = (List<Message>) findSpecificWithId(offset, limit, mid, topicName).get(MESSAGE);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);

		Long startlong = null;
		Long stoplong = null;
		try {
			startlong = MongoUtils.getLongByDate(sdf.parse(startdt));
			stoplong = MongoUtils.getLongByDate(sdf.parse(stopdt));
		} catch (ParseException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error when parse date to Long.", e);
			}
		}
		for (int i = 0; i < list.size(); ++i) {
			if (!(mid > startlong && mid < stoplong))
				list.remove(i);
		}
		return getResponse(list, (long) list.size());

	}

	@SuppressWarnings({ "unchecked" })
	private void convert(DBObject result, Message swallowMessage) {
		BSONTimestamp timestamp = (BSONTimestamp) result.get(ID);
		BSONTimestamp originalTimestamp = (BSONTimestamp) result.get(OID);
		if (originalTimestamp != null)
			swallowMessage.setO_id(originalTimestamp);
		swallowMessage.set_id(timestamp);

		swallowMessage.setC((String) result.get(C));
		swallowMessage.setV((String) result.get(V));
		swallowMessage.setGt((Date) result.get(GT));
		Map<String, String> propertiesBasicDBObject = (Map<String, String>) result.get(P);
		if (propertiesBasicDBObject != null) {
			HashMap<String, String> properties = new HashMap<String, String>(propertiesBasicDBObject);
			swallowMessage.setP(properties.toString());
		}
		Map<String, String> internalPropertiesBasicDBObject = (Map<String, String>) result.get(IP);
		if (internalPropertiesBasicDBObject != null) {
			HashMap<String, String> properties = new HashMap<String, String>(internalPropertiesBasicDBObject);
			swallowMessage.set_p(properties.toString());
		}
		swallowMessage.setS((String) result.get(S));
		swallowMessage.setT((String) result.get(T));
		swallowMessage.setSi((String) result.get(SI));
	}

	private Map<String, Object> findSpecificWithId(int offset, int limit, long mid, String topicName) {
		DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(
				MESSAGE_COLLECTION);
		DBObject query = BasicDBObjectBuilder.start().add(ID, MongoUtils.longToBSONTimestamp(mid)).get();
		DBObject result = collection.findOne(query);
		List<Message> messageList = new ArrayList<Message>();
		if (result != null) {
			Message swallowMessage = new Message();
			try {
				convert(result, swallowMessage);
				messageList.add(swallowMessage);
			} catch (RuntimeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when convert resultset to WebSwallowMessage.", e);
				}
			}
		}
		return getResponse(messageList, (long) messageList.size());
	}

	public WebMongoManager getWebwebMongoManager() {
		return this.webMongoManager;
	}

	private Map<String, Object> getResponse(List<Message> list, Long size) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, size);
		map.put(MESSAGE, list);
		return map;
	}

	@Override
	public Map<String, Object> findMinAndMaxTime(String topicName) {

		Map<String, Object> map = new HashMap<String, Object>();
		Query query1 = new Query();
		query1.with(new Sort(new Sort.Order(Direction.DESC, ID))).limit(1);
		query1.fields().exclude(C);
		List<Message> msgs = this.webMongoManager.getMessageMongoTemplate(topicName).find(query1, Message.class,
				MESSAGE_COLLECTION);
		if (msgs.size() == 0) {
			map.put("max", "");
			map.put("min", "");
			return map;
		}

		Message msg = msgs.get(0);
		map.put("max", convertToStstring(msg.get_id()));

		Query query2 = new Query();
		query2.with(new Sort(new Sort.Order(Direction.ASC, ID))).limit(1);
		query2.fields().exclude(C);
		msg = this.webMongoManager.getMessageMongoTemplate(topicName).find(query2, Message.class, MESSAGE_COLLECTION)
				.get(0);
		map.put("min", convertToStstring(msg.get_id()));

		return map;
	}
	
	private String convertToStstring(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds) * 1000;
		return new SimpleDateFormat(TIMEFORMAT).format(new Date(millions));
	}

	private List<Message> getMessageFromOneSide(Long messageId, int size, DBCollection collection, boolean side, boolean sort) {

		DBObject dbo;
		DBObject orderBy;
		if (side) {
			dbo = BasicDBObjectBuilder.start().add("$gt", MongoUtils.longToBSONTimestamp(messageId)).get();
			orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(1)).get();
		} else {
			if(messageId == 1){
				dbo = BasicDBObjectBuilder.start().add("$lt", MongoUtils.getTimestampByCurTime()).get();
			}else{
				dbo = BasicDBObjectBuilder.start().add("$lt", MongoUtils.longToBSONTimestamp(messageId)).get();
			}
			orderBy = BasicDBObjectBuilder.start().add(ID, Integer.valueOf(-1)).get();
		}
		DBObject query = BasicDBObjectBuilder.start().add(ID, dbo).get();
		DBObject exclude = BasicDBObjectBuilder.start().add(C, 0).get();
		DBCursor cursor = collection.find(query, exclude).sort(orderBy).limit(size);

		List<Message> list = new ArrayList<Message>();
		try {
			while (cursor.hasNext()) {
				DBObject result = cursor.next();
				Message swallowMessage = new Message();
				try {
					convert(result, swallowMessage);
					list.add(swallowMessage);
				} catch (RuntimeException e) {
					logger.error("Error when convert resultset to Message.", e);
				}
			}
		} finally {
			cursor.close();
		}

		if ( messageId == 1 ) {
			return Lists.reverse(list);
		}
		return list;
	}

	@Override
	public List<DBObject> exportMessages(String topicName, String startdt, String stopdt) {
		int maxSize = 1000000;
		List<DBObject> dboList = new ArrayList<DBObject>();
		DBCollection collection = this.webMongoManager.getMessageMongoTemplate(topicName).getCollection(MESSAGE_COLLECTION);
		SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
		Long startlong = null;
		Long stoplong = null;
		try {
			startlong = MongoUtils.getLongByDate(sdf.parse(startdt));
			stoplong = MongoUtils.getLongByDate(sdf.parse(stopdt));
		} catch (ParseException e) {
			logger.error("Error when parse date to Long.", e);
			return dboList;
		}
		DBObject query = BasicDBObjectBuilder
				.start()
				.add(ID,
						BasicDBObjectBuilder.start().add("$gt", MongoUtils.longToBSONTimestamp(startlong))
								.add("$lt", MongoUtils.longToBSONTimestamp(stoplong)).get()).get();
		DBObject orderBy = BasicDBObjectBuilder.start().add(ID, -1).get();
		DBCursor cursor = collection.find(query).sort(orderBy).limit(maxSize);
		DBCursor dbc = collection.find().limit(1);
		if (dbc.hasNext()) {
			DBObject result = dbc.next();
			int size = 0;
			try {
				size = result.toString().getBytes("UTF-8").length;
				if (size > 1000) {
					maxSize = maxSize * 1000 / size;
				}
				dboList = cursor.toArray(maxSize);
			} catch (UnsupportedEncodingException e) {
				logger.info("Encoding error of cursor result");
			} finally {
				dbc.close();
				cursor.close();
			}
		}
		return dboList;

	}

}
