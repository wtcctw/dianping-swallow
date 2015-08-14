package com.dianping.swallow.web.dashboard.model;

import java.util.Date;
import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年8月14日上午11:31:36
 */
public class ResultEntry {
	
	private List<Entry> result;
	
	private Date time;

	public List<Entry> getResult() {
		return result;
	}

	public void setResult(List<Entry> result) {
		this.result = result;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ResultEntry [result=" + result + ", time=" + time + "]";
	}

}
