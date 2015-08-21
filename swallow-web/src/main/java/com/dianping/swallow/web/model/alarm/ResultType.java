package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 *         2015年8月5日 上午10:44:24
 */
public enum ResultType {
	/**
	 * 
	 */
	SUCCESS("成功"),
	/**
	 * 
	 */
	FAILED("失败"),
	/**
	 * 
	 */
	FAILED_CONNECTION_TIMEOUT("链接超时"),
	/**
	 * 
	 */
	FAILED_SOCKET_TIMEOUT("SOCKET超时"),
	/**
	 * 
	 */
	FAILED_CONNECT("链接问题"),
	/**
	 * 
	 */
	FAILED_HOST_UNKNOWN("未知主机"),
	/**
	 * 
	 */
	FAILED_NOPERSON("无收件人");

	private ResultType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private String desc;

	public boolean isSuccess() {
		return this == SUCCESS;
	}
}
