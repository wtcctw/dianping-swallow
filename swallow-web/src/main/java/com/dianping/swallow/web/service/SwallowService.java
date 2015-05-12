package com.dianping.swallow.web.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mingdongli
 *		2015年5月12日 上午10:32:29
 */
public interface SwallowService {
	
	boolean checkVisitIsValid(HttpServletRequest request);
	
	boolean checkVisitIsValid(HttpServletRequest request, String topic);
}
