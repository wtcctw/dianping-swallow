package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.cmdb.EnvDevice;
import com.dianping.swallow.web.model.cmdb.IPDesc;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:47:09
 */
public interface CmdbService {

	/**
	 * get ipDesc model by ip
	 * 
	 * @param ip
	 * @return
	 */
	IPDesc getIpDesc(String ip);

	/**
	 * get ipDesc model by ip
	 * 
	 * @param ip
	 * @return
	 */
	List<EnvDevice> getEnvDevices(String project);

}
