package com.dianping.swallow.web.manager;

import com.dianping.swallow.web.model.cmdb.IPDesc;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:52:44
 */
public interface IPResourceManager {

	/**
	 * get ipdesc from cmdb or db
	 * 
	 * @param ip
	 * @return
	 */
	IPDesc getIPDesc(String ip);

}
