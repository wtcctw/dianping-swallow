package com.dianping.swallow.web.service;

/**
 * @author mingdongli
 *
 * 2015年6月4日下午4:34:17
 */
public interface IdentityRecognitionService extends SwallowService{

	/**
	 * 
	 * @param username 用户名
	 */
	boolean isAdmin(String username);
	
	/**
	 * 
	 * @param username 用户名
	 */
	boolean isUser(String username);
}
