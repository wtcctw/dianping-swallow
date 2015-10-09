package com.dianping.swallow.web.model.resource;

import java.util.List;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author mingdongli
 *
 * 2015年9月17日下午7:52:52
 */
@Document(collection = "MONGO_RESOURCE")
public class MongoResource extends BaseResource{
	
	@Indexed(name = "IX_IP", direction = IndexDirection.ASCENDING)
	private String ip;
	
	@Indexed(name = "IX_CATALOG", direction = IndexDirection.ASCENDING)
	private String catalog;

	private Float load;
    
    private Float disk;
    
    private String dba;
    
    private int threads;
    
    private Integer qps;
    
    private int dev_cnt;
    
    private List<String> topics;
    
    private MongoType mongoType;
    
	public MongoType getMongoType() {
		return mongoType;
	}

	public void setMongoType(MongoType mongoType) {
		this.mongoType = mongoType;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
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

	@Override
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(ip)) {
			return true;
		}
		return false;
	}

}
