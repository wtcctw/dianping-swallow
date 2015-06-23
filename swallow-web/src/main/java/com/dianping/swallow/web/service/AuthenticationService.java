package com.dianping.swallow.web.service;

/**
 * @author mingdongli 2015年5月13日 下午4:32:29
 */
public interface AuthenticationService {
	
	public static final int ADMINI = 0;

	public static final int USER = 3;

	public static final int VISITOR = 10;
	
	/**
	 * 
	 * @param username  通行证
	 * @param topic     topic名称
	 * @param uri       访问uri
	 */
	boolean isValid(String username, String topic, String uri);
	
	/**
	 * 
	 * @param username  通行证
	 */
	int checkVisitType(String username);
}
