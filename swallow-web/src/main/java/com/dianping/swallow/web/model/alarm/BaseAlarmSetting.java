package com.dianping.swallow.web.model.alarm;

import java.util.Date;

import org.springframework.data.annotation.Id;
/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:46:21
 */
public class BaseAlarmSetting {
	
	@Id
	private String id;
	
	private Date createTime;
	
	private Date updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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