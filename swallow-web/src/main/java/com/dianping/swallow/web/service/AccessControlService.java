package com.dianping.swallow.web.service;

import javax.servlet.http.HttpServletRequest;


/**
 * @author mingdongli
 *		2015年5月13日 下午4:32:29
 */
public interface AccessControlService extends SwallowService{

	/**
	 * 检查用户是否有权限访问
	 * @param request
	 */
	boolean checkVisitIsValid(HttpServletRequest request);
	
	/**
	 * 检查用户是否有权限访问
	 * @param request
	 * @param topic  topic名称
	 */
	boolean checkVisitIsValid(HttpServletRequest request, String topic);
}
