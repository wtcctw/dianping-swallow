package com.dianping.swallow.common.internal.config;

import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.MongoClientOptions.Builder;

/**
 * 负责载入Mongo本地配置
 * 
 * @author wukezhu
 */
public class MongoConfig extends AbstractConfig {

	private boolean slaveOk = true;
	private boolean socketKeepAlive = true;
	private int socketTimeout = 5000;
	private int connectionsPerHost = 100;
	private int threadsAllowedToBlockForConnectionMultiplier = 5;
	private int w = 1;
	private int wtimeout = 5000;
	private boolean fsync = false;
	private int connectTimeout = 2000;
	private int maxWaitTime = 2000;
	private boolean safe = true;

	public MongoConfig(String fileName) {
		loadLocalConfig(fileName);
	}

	public MongoClientOptions buildMongoOptions() {

		Builder builder = MongoClientOptions.builder();

		builder.socketKeepAlive(isSocketKeepAlive());
		builder.socketTimeout(getSocketTimeout());
		builder.connectionsPerHost(getConnectionsPerHost());
		builder.threadsAllowedToBlockForConnectionMultiplier(getThreadsAllowedToBlockForConnectionMultiplier());
		builder.connectTimeout(getConnectTimeout());
		builder.maxWaitTime(getMaxWaitTime());

		builder.writeConcern(new WriteConcern(getW(), getWtimeout(), isFsync()));
		builder.readPreference(ReadPreference.secondaryPreferred());

		return builder.build();
	}

	public boolean isSlaveOk() {
		return slaveOk;
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
}
