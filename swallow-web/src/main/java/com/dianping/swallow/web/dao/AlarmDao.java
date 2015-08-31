package com.dianping.swallow.web.dao;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.alarm.Alarm;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:32:52
 */
public interface AlarmDao extends Dao {

	/**
	 * insert
	 * 
	 * @param alarm
	 * @return
	 */
	boolean insert(Alarm alarm);

	/**
	 * update
	 * 
	 * @param alarm
	 * @return
	 */
	boolean update(Alarm alarm);

	/**
	 * delete by id
	 * 
	 * @param ipDesc
	 * @return
	 */
	int deleteById(String id);

	/**
	 * find by id
	 * 
	 * @param id
	 * @return
	 */
	Alarm findById(String id);

	
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
	Pair<List<Alarm>, Long> findByPage(AlarmParam alarmParam);

	/**
	 * find by eventId
	 * 
	 * @param eventId
	 * @return
	 */
	Alarm findByEventId(long eventId);

	public static class AlarmParam {
		
		private String receiver;
		
		private String related;
		
		private String subRelated;
		
		private Date startTime;
		
		private Date endTime;
		
		private int offset;
		
		private int limit;

		public String getReceiver() {
			return receiver;
		}

		public AlarmParam setReceiver(String receiver) {
			this.receiver = receiver;
			return this;
		}

		public String getRelated() {
			return related;
		}

		public AlarmParam setRelated(String related) {
			this.related = related;
			return this;
		}

		public String getSubRelated() {
			return subRelated;
		}

		public AlarmParam setSubRelated(String subRelated) {
			this.subRelated = subRelated;
			return this;
		}

		public Date getStartTime() {
			return startTime;
		}

		public AlarmParam setStartTime(Date startTime) {
			this.startTime = startTime;
			return this;
		}

		public Date getEndTime() {
			return endTime;
		}

		public AlarmParam setEndTime(Date endTime) {
			this.endTime = endTime;
			return this;
		}

		public int getOffset() {
			return offset;
		}

		public AlarmParam setOffset(int offset) {
			this.offset = offset;
			return this;
		}

		public int getLimit() {
			return limit;
		}

		public AlarmParam setLimit(int limit) {
			this.limit = limit;
			return this;
		}
	}
}
