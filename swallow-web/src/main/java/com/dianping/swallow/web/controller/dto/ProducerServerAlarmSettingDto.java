package com.dianping.swallow.web.controller.dto;

import java.util.Date;

public class ProducerServerAlarmSettingDto {
	private String whiteList;
	
	private long peak;
	
	private long valley;

	private int fluctuation;
	
	private Date createTime;
	
	private Date updateTime;

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

	public long getPeak() {
		return peak;
	}

	public void setPeak(long peak) {
		this.peak = peak;
	}

	public long getValley() {
		return valley;
	}

	public void setValley(long valley) {
		this.valley = valley;
	}

	public int getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(int fluctuation) {
		this.fluctuation = fluctuation;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	} 

}
