package com.dianping.swallow.common.internal.dao;

import com.mongodb.DBCollection;

/**
 * @author mengwenchao
 * 
 *         2015年3月23日 下午5:10:44
 */
public interface MongoManager {


	DBCollection getMessageCollection(String topicName);
	
	DBCollection getMessageCollection(String topicName, String consumerId);

	DBCollection getAckCollection(String topicName, String consumerId);

	DBCollection getAckCollection(String topicName, String consumerId, boolean isBackup);

	DBCollection getHeartbeatCollection(String ip);

	void cleanMessageCollection(String topicName, String consumerId);
}
