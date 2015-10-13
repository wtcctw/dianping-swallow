package com.dianping.swallow.web.model.resource;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mingdongli
 *
 *         2015年8月18日下午6:17:12
 */
public abstract class BaseResource {

	@Transient
	protected static final String DEFAULT_RECORD = "default";

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
	
	@JsonIgnore
	public abstract boolean isDefault();

}
