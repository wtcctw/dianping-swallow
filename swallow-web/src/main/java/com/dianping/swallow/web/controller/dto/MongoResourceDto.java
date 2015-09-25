package com.dianping.swallow.web.controller.dto;



/**
 * @author mingdongli
 *
 * 2015年9月18日上午9:22:43
 */
public class MongoResourceDto extends BaseDto{
	
	private String id;

	private String ip;
	
    private Float load;
    
    private Float disk;
    
    private String dba;
    
    private String catalog;
    
    private int threads;
    
    private Integer qps;
    
    private int dev_cnt;
    
    private String topics;
    
    private String mongoType;
    
	public String getMongoType() {
		return mongoType;
	}

	public void setMongoType(String mongoType) {
		this.mongoType = mongoType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Float getLoad() {
		return load;
	}

	public void setLoad(Float load) {
		this.load = load;
	}

	public Float getDisk() {
		return disk;
	}

	public void setDisk(Float disk) {
		this.disk = disk;
	}

	public String getDba() {
		return dba;
	}

	public void setDba(String dba) {
		this.dba = dba;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public Integer getQps() {
		return qps;
	}

	public void setQps(Integer qps) {
		this.qps = qps;
	}

	public int getDev_cnt() {
		return dev_cnt;
	}

	public void setDev_cnt(int dev_cnt) {
		this.dev_cnt = dev_cnt;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}
    
}
