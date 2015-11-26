package com.dianping.swallow.test.man.mongo;

import java.text.ParseException;
import java.util.Date;

import org.bson.types.BSONTimestamp;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoMessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoManager;
import com.dianping.swallow.common.internal.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 下午5:40:00
 */
public class AbstractMongoUtil {
	
	private final String ID = "_id";
	
	private final String TYPE = "t";
	
	@JsonIgnore
	private MongoManager mongoManager;
	
	@JsonIgnore
	private MessageDAO 	dao;
	
	protected String topic;
	protected Date 	  start, end;
	protected String type;
	protected String cmd;
	
	public AbstractMongoUtil(){
		
		mongoManager = new DefaultMongoManager();
		dao = new MongoMessageDAO(mongoManager);
	}
		
	public long count(String topic, Date start, Date end, String type){
		
		DBCollection col = mongoManager.getMessageCollection(topic);
		
		DBObject query = condition(start, end);
		if(type != null){
			query.put(TYPE, type);
		}
		return col.count(query);
	}


	private DBObject condition(Date start, Date end) {
		
		DBObject condition = new BasicDBObject();
		condition.put("$gte", getBsonTimestamp(start));
		condition.put("$lt", getBsonTimestamp(end));
		return new BasicDBObject(ID, condition);
	}

	private BSONTimestamp getBsonTimestamp(Date start) {
		return new BSONTimestamp((int) (start.getTime()/1000), 0);
	}

	protected void start() throws ParseException {
		
		getArgs();
		
		if(cmd.equals("count")){
			long count = count(topic, start, end, type);
			System.out.println("Count:" + count);
		}
		
		
	}

	private void getArgs() throws ParseException {
		
		topic = getIfNotEmpty(System.getProperty("topic"), "swallow-test-integrated");
		type = getIfNotEmpty(System.getProperty("type"), type);
		String startTime = getIfNotEmpty(System.getProperty("start"), "20150515194849");
		String endTime = getIfNotEmpty(System.getProperty("end"), "20150521240000");;
		
		start = DateUtils.fromSimpleFormat(startTime);
		end = DateUtils.fromSimpleFormat(endTime);
		cmd = getIfNotEmpty(System.getProperty("cmd"), "count");
		
		System.out.println(JsonBinder.getNonEmptyBinder().toJson(this));
	}

	private String getIfNotEmpty(String property, String str) {
		
		if(property == null){
			return str;
		}
		return property;
	}


}
