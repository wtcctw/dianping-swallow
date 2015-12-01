package com.dianping.swallow.common.internal.dao.impl.mongodb;

import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.impl.AbstractClusterFactory;
import com.mongodb.MongoClientOptions;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午10:20:30
 */
public class MongoClusterFactory extends AbstractClusterFactory{
	
	private static final String MONGO_CONFIG_FILENAME = "swallow-mongo.properties";

	private MongoClientOptions mongoOptions;

	private String mongoConfigLionSuffix;
	
	public MongoClusterFactory(){
	}
	
	public MongoClusterFactory(String mongoConfigLionSuffix) {
		this.mongoConfigLionSuffix = mongoConfigLionSuffix;
	}

	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		MongoConfig config = new MongoConfig(MONGO_CONFIG_FILENAME, mongoConfigLionSuffix);
		mongoOptions = config.buildMongoOptions();
		if (logger.isInfoEnabled()) {
			logger.info("MongoOptions=" + mongoOptions.toString());
		}
	}

	@Override
	public Cluster createCluster(String address) {
		
		return new MongoCluster(mongoOptions, address);
	}

	@Override
	public boolean accepts(String url) {
		
		return isMongoUrl(getTypeDesc(url));
	}

	private boolean isMongoUrl(String type) {
		
		if(type == null || type.equalsIgnoreCase(MongoCluster.schema)){
			return true;
		}
		
		return false;
	}

	public MongoClientOptions getMongoOptions(){
		return mongoOptions;
	}
}
