package com.dianping.swallow.common.internal.config;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.internal.util.StringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.MongoClientOptions.Builder;

/**
 * 负责载入Mongo本地配置
 * 
 * @author wukezhu
 */
public class MongoConfig extends AbstractLionConfig {

	private static final String MONGO_CONIFG_BASIC_SUFFIX = "mongoconfig";
	
	private boolean socketKeepAlive = true;
	private int socketTimeout = 5000;
	private int connectionsPerHost = 100;
	private int threadsAllowedToBlockForConnectionMultiplier = 5;
	
	private int w = 1;
	private int wtimeout = 5000;
	private boolean fsync = false;
	private boolean journal = false;
	
	private int connectTimeout = 2000;
	private int maxWaitTime = 2000;
	private boolean safe = true;
	private boolean readFromMaster = false;
	
	/**
	 * 例如 use:product,use:another;use:third
	 */
	private String tags;

	public MongoConfig(String fileName, String suffix, boolean isUseLion) {
		super(fileName, StringUtils.join(SPLIT, MONGO_CONIFG_BASIC_SUFFIX, suffix), isUseLion);
		loadConfig();
	}

	public MongoConfig(String fileName, String suffix) {
		this(fileName, suffix, true);
	}

	public MongoConfig(String fileName) {
		this(fileName, null, true);
	}
	
	public MongoClientOptions buildMongoOptions() {

		Builder builder = MongoClientOptions.builder();

		builder.socketKeepAlive(isSocketKeepAlive());
		builder.socketTimeout(getSocketTimeout());
		builder.connectionsPerHost(getConnectionsPerHost());
		builder.threadsAllowedToBlockForConnectionMultiplier(getThreadsAllowedToBlockForConnectionMultiplier());
		builder.connectTimeout(getConnectTimeout());
		builder.maxWaitTime(getMaxWaitTime());

		builder.writeConcern(new WriteConcern(getW(), getWtimeout(), isFsync(), isJournal()));
		builder.readPreference(buildReadPreference());

		return builder.build();
	}

	public ReadPreference buildReadPreference() {
		
		if(StringUtils.isEmpty(tags)){
			return readPreference(readFromMaster, null);
		}
		
		List<DBObject>  tagSets = new LinkedList<DBObject>(); 
		
		String []strTagSets = tags.split(";");
		
		for(String strTagSet : strTagSets){
			
			if(StringUtils.isEmpty(strTagSet)){
				continue;
			}
			
			DBObject tagSet = new BasicDBObject();
			String []tags = strTagSet.split(",");
			for(String tag : tags){
				
				if(StringUtils.isEmpty(tag)){
					continue;
				}
				
				String []keyValue = tag.split(":");
				if(keyValue.length != 2){
					throw new IllegalArgumentException("wrong desc, should be: key:value, but " + tag);
				}
				tagSet.put(keyValue[0].trim(), keyValue[1].trim());
			}
			tagSets.add(tagSet);
		}

		if(logger.isInfoEnabled()){
			logger.info("[buildReadPreference][tagSets]" + tagSets);
		}
		int size = tagSets.size();
		if(size == 0){
			throw new IllegalArgumentException("wrong tags " + tags);
		}
		
		return readPreference(readFromMaster, tagSets);
	}

	private ReadPreference readPreference(boolean readFromMaster, List<DBObject> tagSets) {
		
		if(readFromMaster){
			if(tagSets != null && tagSets.size() != 0){
				return ReadPreference.secondaryPreferred(tagSets.get(0), tagSets.subList(1, tagSets.size()).toArray(new DBObject[0]));
			}
			return ReadPreference.secondaryPreferred();
		}

		if(tagSets != null && tagSets.size() != 0){
			return ReadPreference.secondary(tagSets.get(0), tagSets.subList(1, tagSets.size()).toArray(new DBObject[0]));
		}
		return ReadPreference.secondary();
		
	}

	public boolean isSocketKeepAlive() {
		return socketKeepAlive;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public int getConnectionsPerHost() {
		return connectionsPerHost;
	}

	public int getThreadsAllowedToBlockForConnectionMultiplier() {
		return threadsAllowedToBlockForConnectionMultiplier;
	}

	public int getW() {
		return w;
	}

	public int getWtimeout() {
		return wtimeout;
	}

	public boolean isFsync() {
		return fsync;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public boolean isSafe() {
		return safe;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * @return the journal
	 */
	public boolean isJournal() {
		return journal;
	}

	/**
	 * @param journal the journal to set
	 */
	public void setJournal(boolean journal) {
		this.journal = journal;
	}
}
