package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.resource.ServerResource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午5:02:42
 */
public interface ServerResourceService {

	ServerResource findByIp(String ip);

	ServerResource findByHostname(String hostname);

	ServerResource findDefault();

}
