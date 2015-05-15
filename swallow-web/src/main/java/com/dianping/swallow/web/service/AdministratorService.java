package com.dianping.swallow.web.service;

import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/*-
 * @author mingdongli
 *
 * 2015年5月14日下午8:05:01
 */
public interface AdministratorService extends SwallowService{
	
	Map<String, Object> adminQuery(String offset, String limit, String name, String role) throws UnknownHostException;

	Object queryAdmin(HttpServletRequest request);
	
	void createAdmin(String name, int auth);
	
	void removeAdmin(String name);
	
	Object queryAllVisits();
	
	void saveVisitAdmin(String name);
	
}
