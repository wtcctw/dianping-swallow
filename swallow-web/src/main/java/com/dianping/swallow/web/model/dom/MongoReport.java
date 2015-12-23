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

	private String create_date;

	private String dba;

	private int threads;

	private int dev_cnt;

	private int _id;

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

	//在反序列化中使用，不要删除
	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getDba() {
		return dba;
	}

	public void setDba(String dba) {
		this.dba = dba;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public int getDev_cnt() {
		return dev_cnt;
	}

	public void setDev_cnt(int dev_cnt) {
		this.dev_cnt = dev_cnt;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
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
