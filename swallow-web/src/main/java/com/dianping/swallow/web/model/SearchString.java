package com.dianping.swallow.web.model;

import org.springframework.data.annotation.Id;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:51
 */
public class SearchString {
	@Id
	private String id;

	private String admin;

	public SearchString() {
	}

	public SearchString(String id, String admin) {
		this.id = id;
		this.admin = admin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDept() {
		return admin;
	}

	public void setDept(String admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		return id + "::" + admin;
	}
}
