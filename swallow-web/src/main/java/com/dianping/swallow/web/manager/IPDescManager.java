package com.dianping.swallow.web.manager;

import com.dianping.swallow.web.model.cmdb.IPDesc;

/**
 * 
 * @author qiyin
 *
 */

public interface IPDescManager {
	
	/**
	 * get ipdesc from cmdb or db
	 * @param ip
	 * @return
	 */
	public IPDesc getIPDesc(String ip);

}
