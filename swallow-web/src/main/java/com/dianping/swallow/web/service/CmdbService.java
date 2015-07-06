package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.cmdb.EnvDevice;
import com.dianping.swallow.web.model.cmdb.IPDesc;

/**
 * 
 * @author qiyin
 *
 */
public interface CmdbService  extends SwallowService {
	
	/**
	 * get ipDesc model by ip
	 * @param ip
	 * @return
	 */
	public IPDesc getIpDesc(String ip);
	/**
	 * get ipDesc model by ip
	 * @param ip
	 * @return
	 */
	public List<EnvDevice> getEnvDevices(String project);
	
}
