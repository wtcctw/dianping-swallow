package com.dianping.swallow.web.service;

import javax.servlet.http.HttpServletRequest;


/**
 * @author mingdongli
 *		2015年5月13日 下午4:32:29
 */
public interface AccessControlService extends SwallowService{

	boolean checkVisitIsValid(HttpServletRequest request);
	
	boolean checkVisitIsValid(HttpServletRequest request, String topic);
}
