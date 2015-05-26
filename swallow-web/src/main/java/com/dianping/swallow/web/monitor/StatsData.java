package com.dianping.swallow.web.monitor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.util.DateUtils;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:49:03
 */
public class StatsData {
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

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
	
	
	/**
	 * 插值，最小值于startTime对齐
	 * @param startMin
	 */
	public void minToTime(long startTime) {
		if(startTime <= 0 || start < startTime){
			throw new IllegalArgumentException("illegal startTime " + startTime + ", " + start);
		}
		
		long count = (start - startTime)/intervalTimeSeconds/1000;
		if(logger.isInfoEnabled()){
			logger.info("[minToTime][insertdata count]["+this+"]" + count + "," + DateUtils.toPrettyFormat(startTime) + "," + DateUtils.toPrettyFormat(start));
		}
		for(int i=0;i<count;i++){
			data.add(0, 0L);
		}
		
		start = startTime;
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

	public StatsDataDesc getInfo() {
		return info;
	}

	public void setInfo(StatsDataDesc info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return info.getDesc() + ",start:" + DateUtils.toPrettyFormat(start) + ", interval:" + intervalTimeSeconds + ",dataLen:" + data.size();   
	}

}
