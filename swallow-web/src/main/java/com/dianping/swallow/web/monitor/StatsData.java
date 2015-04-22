package com.dianping.swallow.web.monitor;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:49:03
 */
public class StatsData {
	
	private long total;
	private int []data;
	
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int [] getData() {
		return data;
	}

	public void setData(int [] data) {
		this.data = data;
	}
}
