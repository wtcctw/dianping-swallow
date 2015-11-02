package com.dianping.swallow.web.model.dom;



/**
 * @author mingdongli
 *
 * 2015年9月9日下午1:48:42
 */
public class MongoReport implements Comparable<MongoReport>{

	private Float load;
    
    private Float disk;
    
    private String catalog;
    
    private Float io;
    
    private Integer qps;
    
    private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public float getLoad() {
		return load;
	}

	public void setLoad(float load) {
		this.load = load;
	}

	public float getDisk() {
		return disk;
	}

	public void setDisk(float disk) {
		this.disk = disk;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public float getIo() {
		return io;
	}

	public void setIo(float io) {
		this.io = io;
	}

	public int getQps() {
		return qps;
	}

	public void setQps(int qps) {
		this.qps = qps;
	}

	@Override
	public int compareTo(MongoReport that) {

		int numAlarm = disk.compareTo(that.disk); // 升序
		if (numAlarm == 0) {
			return qps.compareTo(that.qps);
		} else {
			return numAlarm;
		}
	}
	
}
