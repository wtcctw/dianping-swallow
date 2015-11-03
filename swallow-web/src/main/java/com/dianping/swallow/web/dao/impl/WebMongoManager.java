package com.dianping.swallow.web.dao.impl;



import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * @author mingdongli 
 * 2015年4月8日 下午6:31:13
 */
public interface WebMongoManager {

	MongoTemplate getMessageMongoTemplate(String topicName);

}
