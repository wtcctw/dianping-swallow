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
	 * 
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
	 * @param createTime
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Alarm> findByCreateTime(Date createTime, int offset, int limit);

	/**
	 * find by receiver and createTime
	 * @param receiver
	 * @param timeStart
	 * @param timeEnd
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Alarm> findByReceiverAndTime(String receiver, Date startTime, Date endTime, int offset, int limit);

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
	
	/**
	 * find count by receiver and createTime
	 * @param receiver
	 * @param timeStart
	 * @param timeEnd
	 * @return
	 */
	public long countByReceiverAndTime(String receiver, Date startTime, Date endTime);

}
