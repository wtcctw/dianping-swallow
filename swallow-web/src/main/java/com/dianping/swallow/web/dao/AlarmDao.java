package com.dianping.swallow.web.dao;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.model.alarm.Alarm;

/**
*
* @author qiyin
*
*/
public interface AlarmDao extends Dao {

	/**
	 * insert
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean insert(Alarm alarm);

	/**
	 * update
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean update(Alarm alarm);

	/**
	 * delete by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	public int deleteById(String id);
	
	/**
	 * find by id
	 * @param id
	 * @return
	 */
	public Alarm findById(String id);

	/**
	 * find by receiver
	 * 
	 * @param Alarm
	 * @return
	 */
	public List<Alarm> findByReceiver(String receiver, int offset, int limit);

	/**
	 * find by createTime
	 * 
	 * @param ipDesc
	 * @return
	 */
	public List<Alarm> findByCreateTime(Date createTime, int offset, int limit);

	/**
	 * find count by createTime
	 * 
	 * @param 
	 * @return
	 */
	public long countByCreateTime(Date createTime);
	
	/**
	 * find count by receiver
	 * 
	 * @param 
	 * @return
	 */
	public long countByReceiver(String receiver);

}
