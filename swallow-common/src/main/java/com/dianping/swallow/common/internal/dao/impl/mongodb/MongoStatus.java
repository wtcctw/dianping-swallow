package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.List;

import com.dianping.swallow.common.internal.monitor.ComponentStatus;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年6月26日 下午5:01:07
 */
public class MongoStatus implements ComponentStatus{
		
	private List<ServerAddress> serverAddressList;

	private MongoClientOptions mongoClientOptions;

	private List<ServerAddress> addAddress;

	/**
	 * for json deserialize
	 */
	public MongoStatus(){
		
	}
	
	public MongoStatus(MongoClient client) {
		
		this.serverAddressList  = client.getServerAddressList();
		this.mongoClientOptions =  client.getMongoClientOptions();
		this.addAddress = client.getAllAddress();
		
	}

	
	public List<ServerAddress> getServerAddressList() {
		return serverAddressList;
	}

	public MongoClientOptions getMongoClientOptions() {
		return mongoClientOptions;
	}

	public List<ServerAddress> getAddAddress() {
		return addAddress;
	}

}
