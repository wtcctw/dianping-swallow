package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.dianping.swallow.common.internal.dao.impl.AbstractCluster;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:25:27
 */
public class MongoCluster extends AbstractCluster{

	public static final String schema = "mongodb://";
	
	private MongoClientOptions mongoOptions;
	
	private MongoClient mongoClient;
	
	public MongoCluster(MongoClientOptions mongoOptions, String address){
		
		super(address);
		
		this.mongoOptions = mongoOptions;
	}
	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		mongoClient = new MongoClient(toMongoSeeds(getSeeds()), mongoOptions);
		
	}

	@Override
	protected void doDispose() throws Exception {
		super.doDispose();
		
		if(mongoClient != null){
			mongoClient.close();
		}
	}
	
	private List<ServerAddress> toMongoSeeds(List<InetSocketAddress> seeds) {
		
		List<ServerAddress>  result = new ArrayList<ServerAddress>(seeds.size());
		
		for(InetSocketAddress socketAddress : seeds){
			
			result.add(new ServerAddress(socketAddress));
		}
		
		return result;
	}

	
	@Override
	protected List<InetSocketAddress> build(String address) {
		
		if (address.startsWith(schema)) {
			address = address.substring(schema.length());
		}
		
		String[] hostPortArr = address.split(splitSpaces(","));
		
		List<InetSocketAddress> result = new ArrayList<InetSocketAddress>();
		
		for (int i = 0; i < hostPortArr.length; i++) {
			
			String[] pair = hostPortArr[i].split(splitSpaces(":"));
			if(pair.length != 2){
				throw new IllegalArgumentException("bad mongo address:" + address);
			}
			try {
				result.add(new InetSocketAddress(pair[0].trim(), Integer.parseInt(pair[1].trim())));
			} catch (Exception e) {
				throw new IllegalArgumentException(
						e.getMessage()
								+ ". Bad format of mongo address："
								+ address
								+ ". The correct format is mongodb://<host>:<port>,<host>:<port>",
						e);
			}
		}
		
		return result;
	}
	
	public MongoClient getMongoClient(){

		return mongoClient;
	}

	public MongoClientOptions getMongoOptions() {
		return mongoOptions;
	}

}
