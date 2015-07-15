package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年7月9日 上午11:05:11
 */
public class MongoContainer {
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	
	private MongoClientOptions mongoOptions;

	private Set<MongoClient> mongoSet = new HashSet<MongoClient>();
	private Map<String, MongoClient> mongos = new ConcurrentHashMap<String, MongoClient>();

	public MongoContainer(MongoClientOptions options) {
		
		this.mongoOptions = options;
	}
	
	public MongoClient getMongo(String url){
		
		MongoClient mongo = mongos.get(url);  
		if( mongo != null){
			return mongo;
		}
		
		return createOrUseExistingMongo(url);
	}


	private synchronized MongoClient createOrUseExistingMongo(String uri) {
		
		List<ServerAddress> replicaSetSeeds = MongoUtils.parseUriToAddressList(uri);

		List<ServerAddress> servers = null;
		for(MongoClient mongo : mongoSet){
			
			servers = mongo.getAllAddress();
			
			if(seedIn(servers, replicaSetSeeds)){
				if(logger.isInfoEnabled()){
					logger.info("[createOrUseExistingMongo][use exist mongo]");
				}
				return mongo;
			}else{
				try{
					servers = mongo.getServerAddressList();
					if(seedIn(servers, replicaSetSeeds)){
						if(logger.isInfoEnabled()){
							logger.info("[createOrUseExistingMongo][use exist mongo]");
						}
						return mongo;
					}
				}catch(MongoException e){
					logger.warn("[createOrUseExistingMongo]", e);
				}
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[createMongo]" + replicaSetSeeds);
		}
		
		MongoClient mongo = new MongoClient(replicaSetSeeds, mongoOptions);
		
		mongoSet.add(mongo);
		mongos.put(uri, mongo);
		return mongo;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean seedIn(List allAddress, List seeds) {

		boolean result = false;
		
		if (allAddress != null && seeds != null) {
			result = allAddress.containsAll(seeds);
		}
		if(logger.isDebugEnabled()){
			logger.debug("[seedIn][" + result + "]" + allAddress + "," + seeds);
		}
		return result;
	}

	public int mongoSize(){
		return mongos.size();
	}

	public void closeAllMongo() {
		
		if(logger.isInfoEnabled()){
			logger.info("[closeAllMongo]");
		}
		
		for(MongoClient mongo : mongoSet){
			mongo.close();
		}
		mongos.clear();
		mongoSet.clear();
	}

	@SuppressWarnings("unused")
	private void closeMongo(MongoClient mongo) {
		
		if (logger.isInfoEnabled()) {
			logger.info("[closeMongo]" + mongo);
		}
		
		boolean contains = mongoSet.remove(mongo);
		if(!contains){
			
			String errorMessage = "[close unexist mongo]" + mongo + "," + mongos;
			logger.error(errorMessage);
			return;
		}
		
		for(Entry<String, MongoClient> entry : mongos.entrySet()){
			
			String key = entry.getKey();
			MongoClient value = entry.getValue();
			if(value == mongo){
				if(logger.isInfoEnabled()){
					logger.info("[closeMongo][removeKey]" + key);
				}
				mongos.remove(key);
			}
		}
		
		mongo.close();
	}

	public Collection<MongoClient> getAllMongo(){
		
		return Collections.unmodifiableCollection(mongoSet);
	}
}
