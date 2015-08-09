package com.dianping.swallow.web.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.alarm.Alarm;

/**
 *
 * @author qiyin
 * 
 */
public interface AlarmService {

	/**
	 * send sms
	 * 
	 * @param mobile
	 * @param title
	 * @param body
	 * @return
	 */
	public boolean sendSms(String mobile, String title, String body);

	/**
	 * send weiXin
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendWeiXin(String email, String title, String content);

	/**
	 * send mail
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendMail(String email, String title, String content);

	/**
	 * send sms
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean sendSms(Alarm alarm, String receiver);

	/**
	 * weiXin alarm
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean sendWeiXin(Alarm alarm, String receiver);

	/**
	 * mail alarm
	 * 
	 * @param emails
	 * @param alarm
	 * @return
	 */
	public boolean sendMail(Set<String> emails, Alarm alarm);

	/**
	 * sms alarm
	 * 
	 * @param mobiles
	 * @param alarm
	 * @return
	 */
	public boolean sendSms(Set<String> mobiles, Alarm alarm);

	/**
	 * weiXin alarm
	 * 
	 * @param emails
	 * @param alarm
	 * @return
	 */
	public boolean sendWeiXin(Set<String> emails, Alarm alarm);

	/**
	 * mail alarm
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean sendMail(Alarm alarm, String receiver);

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
	 * find by page
	 * @param receiver
	 * @param related
	 * @param subRelated
	 * @param startTime
	 * @param endTime
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Pair<List<Alarm>, Long> findByPage(String receiver, String related, Date startTime,
			Date endTime, int offset, int limit);
	
	/**
	 * find by page
	 * @param receiver
	 * @param related
	 * @param subRelated
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
