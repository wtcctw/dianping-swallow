package com.dianping.swallow.common.internal.dao.impl.mongodb;


import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;

/**
 * @author mengwenchao
 * 
 *         2015年3月23日 下午5:10:44
 */
public interface MongoManager {

	public static int ORDER = ClusterManager.ORDER + 1;

	DBCollection getMessageCollection(String topicName);
	
	DBCollection getMessageCollection(String topicName, String consumerId);

	DBCollection getAckCollection(String topicName, String consumerId);

	DBCollection getAckCollection(String topicName, String consumerId, boolean isBackup);

	DBCollection getHeartbeatCollection(String ip);

	void cleanMessageCollection(String topicName, String consumerId);

	void cleanAckCollection(String topicName, String consumerId, boolean isBackup);
	
	Mongo getMongo(String topicName);
	
	MongoClientOptions getMongoOptions();
	
	int getMongoCount();
}
