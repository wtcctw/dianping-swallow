package com.dianping.swallow.web.dao;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.common.Pair;
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
	 * find by receiver related and time
	 * 
	 * @param receiver
	 * @param related
	 * @param startTime
	 * @param endTime
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Pair<List<Alarm>, Long> findByPage(String receiver, String related, Date startTime, Date endTime,
			int offset, int limit);

	/**
	 * find by receiver related and time
	 * 
	 * @param receiver
	 * @param related
	 * @param startTime
	 * @param endTime
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Pair<List<Alarm>, Long> findByPage(String receiver, String related, String subRelated, Date startTime,
			Date endTime, int offset, int limit);

	/**
	 * find by eventId
	 * @param eventId
	 * @return
	 */
	public Alarm findByEventId(long eventId);
}
