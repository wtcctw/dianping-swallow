package com.dianping.swallow.web.controller.dto;

import java.util.Date;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:58:28
 */
public class ApplicationResourceDto extends BaseResourceDto{

	private String application;

	private String email;

	private String opManager;

	private String opMobile;

	private String opEmail;

	private String dpManager;

	private String dpMobile;

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

}
