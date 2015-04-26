package com.dianping.swallow.web.model;

import org.springframework.data.annotation.Id;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:51
 */
public class SearchProp {
	@Id
	private String id;

	private String dept;

	public SearchProp() {
	}

	public SearchProp(String id, String dept) {
		this.id = id;
		this.dept = dept;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return id + "::" + dept;
	}
}
