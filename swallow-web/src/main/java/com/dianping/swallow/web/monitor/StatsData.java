package com.dianping.swallow.web.monitor;

import java.util.List;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:49:03
 */
public class StatsData {
	
	private StatsDataDesc info;
	private List<Long> data;
	private long start;
	private int  intervalTimeSeconds;
	
	public StatsData(StatsDataDesc info, List<Long> data, long start, int intervalTimeSeconds){
		
		this.info = info;
		this.data = data;
		this.start = start;
		this.intervalTimeSeconds = intervalTimeSeconds;
	}
	
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public List<Long> getData() {
		return data;
	}

	public Long[] getArrayData() {
		Long []ret = new Long[data.size()];
		return data.toArray(ret);
	}

	public void setData(List<Long> data) {
		this.data = data;
	}

	public int getIntervalTimeSeconds() {
		return intervalTimeSeconds;
	}

	public void setIntervalTimeSeconds(int intervalTimeSeconds) {
		this.intervalTimeSeconds = intervalTimeSeconds;
	}

	/**
	 * @return the info
	 */
	public StatsDataDesc getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(StatsDataDesc info) {
		this.info = info;
	}

}
