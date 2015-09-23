package com.dianping.swallow.web.model.log;

import java.util.Date;


/**
 * @author mingdongli
 *
 * 2015年9月22日下午1:51:54
 */
public class Log {
	
	private Date createTime;
	
	private String user;
	
	private String url;
	
	private String parameter;
	
	private String result;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Log [createTime=" + createTime + ", user=" + user + ", url=" + url + ", parameter=" + parameter
				+ ", result=" + result + "]";
	}

}
