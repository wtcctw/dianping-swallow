package com.dianping.swallow.web.model.alarm.backup;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author qiyin
 *
 */
public abstract class AbstractServerAlarmSetting {

	@Id
	private String id;

	private List<String> topicWhiteList;

	private Date createTime;

	private Date updateTime;

	@Override
	public String toString() {
		return "AbstractServerAlarmSetting [id = " + id + ", topicWhiteList = " + topicWhiteList + ", createTime = "
				+ createTime + ", updateTime = " + updateTime + "]";
	}

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

	public List<String> getTopicWhiteList() {
		return topicWhiteList;
	}

	public void setTopicWhiteList(List<String> topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
