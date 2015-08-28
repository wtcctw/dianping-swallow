package com.dianping.swallow.web.controller.dto;

import java.util.Date;

/**
 * @author mingdongli
 *
 *         2015年8月27日下午4:11:02
 */
public class IpResourceDto {

	private String id;

	private String ip;

	private String ipType;

	private boolean alarm;

	private String application;

	private String email;

	private String opManager;

	private String opMobile;

	private String opEmail;

	private String dpManager;

	private String dpMobile;

	private Date createTime;

	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpManager() {
		return opManager;
	}

	public void setOpManager(String opManager) {
		this.opManager = opManager;
	}

	public String getOpMobile() {
		return opMobile;
	}

	public void setOpMobile(String opMobile) {
		this.opMobile = opMobile;
	}

	public String getOpEmail() {
		return opEmail;
	}

	public void setOpEmail(String opEmail) {
		this.opEmail = opEmail;
	}

	public String getDpManager() {
		return dpManager;
	}

	public void setDpManager(String dpManager) {
		this.dpManager = dpManager;
	}

	public String getDpMobile() {
		return dpMobile;
	}

	public void setDpMobile(String dpMobile) {
		this.dpMobile = dpMobile;
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
