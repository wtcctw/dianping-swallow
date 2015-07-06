package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.cmdb.IPDesc;

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
	public IPDesc getIpDesc(String ip);
	
}
