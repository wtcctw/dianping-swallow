package com.dianping.swallow.web.controller.dto;



/**
 * @author mingdongli
 *
 * 2015年9月18日上午9:22:43
 */
public class MongoResourceDto extends BaseQueryDto{
	
	private String id;

	private String ip;
	
    private Float load;
    
    private Float disk;
    
    private String catalog;
    
    private Integer qps;
    
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

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public Integer getQps() {
		return qps;
	}

	public void setQps(Integer qps) {
		this.qps = qps;
	}

}
