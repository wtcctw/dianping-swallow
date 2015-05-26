package com.dianping.swallow.web.service;

/**
 * @author mingdongli 2015年5月13日 下午4:32:29
 */
public interface AccessControlService {

	/**
	 * 检查用户是否有权限访问
	 * 
	 * @param username
	 *            通行证
	 */
	boolean checkVisitIsValid(String username);

	/**
	 * 检查用户是否有权限访问
	 * 
	 * @param username
	 *            通行证
	 * @param topic
	 *            topic名称
	 */
	boolean checkVisitIsValid(String username, String topic);
}
