package com.dianping.swallow.web.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dianping.swallow.web.model.alarm.Alarm;

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
	public Map<String, Object> findByReceiver(String receiver, int offset, int limit);

	/**
	 * find by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	public Map<String, Object> findByCreateTime(Date createTime, int offset, int limit);

	/**
	 * find all
	 * 
	 * @param 
	 * @return
	 */
	public List<Alarm> findAll();

}
