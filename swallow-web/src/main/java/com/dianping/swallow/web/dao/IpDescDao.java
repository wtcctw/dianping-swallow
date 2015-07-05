package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.cmdb.IpDesc;

/**
 * 
 * @author qi.yin
 *
 */
public interface IpDescDao extends Dao {

	/**
	 * insert
	 * 
	 * @param ipDesc
	 * @return
	 */
	public boolean insert(IpDesc ipDesc);

	/**
	 * update
	 * 
	 * @param ipDesc
	 * @return
	 */
	public boolean update(IpDesc ipDesc);

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
	public int deleteByIp(String id);

	/**
	 * find by ip
	 * 
	 * @param ipDesc
	 * @return
	 */
	public IpDesc findByIp(String ip);

	/**
	 * find by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	public IpDesc findById(String id);

	/**
	 * find all
	 * 
	 * @param ipDesc
	 * @return
	 */
	public List<IpDesc> findAll();

}
