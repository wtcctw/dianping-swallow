package com.dianping.swallow.common.internal.dao.impl.kafka;

import com.dianping.swallow.common.internal.config.AbstractLionConfig;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午3:55:16
 */
public class KafkaConfig extends AbstractLionConfig{
	
	private static final String KAFKA_CONIFG_BASIC_SUFFIX = "kafkaconfig";

	private boolean readFromMaster = true;
	private int minBytes = 0;
	private int soTimeout = 5000;
	private int fetchSize = 2*1024*1024;
	private int maxWait = 5000;
	private int fetchRetryCount = 3;
	private int maxConnectionPerHost = 100;
	private String zip = "gzip";

	public KafkaConfig(String fileName, String suffix, boolean isUseLion) {
		super(fileName, StringUtils.join(SPLIT, KAFKA_CONIFG_BASIC_SUFFIX, suffix), isUseLion);
		loadConfig();
	}

	public KafkaConfig(String localFileConfig, String suffix) {
		this(localFileConfig, suffix, true);
	}
	
	public boolean isReadFromMaster() {
		return readFromMaster;
	}

	public void setReadFromMaster(boolean readFromMaster) {
		this.readFromMaster = readFromMaster;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getFetchRetryCount() {
		return fetchRetryCount;
	}

	public void setFetchRetryCount(int fetchRetryCount) {
		this.fetchRetryCount = fetchRetryCount;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public int getMinBytes() {
		return minBytes;
	}

	public void setMinBytes(int minBytes) {
		this.minBytes = minBytes;
	}

	public int getMaxConnectionPerHost() {
		return maxConnectionPerHost;
	}

	public void setMaxConnectionPerHost(int maxConnectionPerHost) {
		this.maxConnectionPerHost = maxConnectionPerHost;
	}

	
}
