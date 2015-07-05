package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.cmdb.IpDesc;

/**
 * 
 * @author qiyin
 *
 */
public interface CmdbService {
	
	/**
	 * get ipDesc model by ip
	 * @param ip
	 * @return
	 */
	public IpDesc getIpDesc(String ip);
	
}
