package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.cmdb.IPDesc;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:41:44
 */
public interface IpDescService {

	/**
	 * insert
	 * 
	 * @param ipDesc
	 * @return
	 */
	boolean insert(IPDesc ipDesc);

	/**
	 * update
	 * 
	 * @param ipDesc
	 * @return
	 */
	boolean update(IPDesc ipDesc);

	/**
	 * delete by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	public int deleteById(String id);

	/**
	 * delete by ip
	 * 
	 * @param ipDesc
	 * @return
	 */
	int deleteByIp(String id);

	/**
	 * find by ip
	 * 
	 * @param ipDesc
	 * @return
	 */
	IPDesc findByIp(String ip);

	/**
	 * find by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	IPDesc findById(String id);

	/**
	 * find all
	 * 
	 * @param ipDesc
	 * @return
	 */
	List<IPDesc> findAll();

}
