package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.AlarmDao.AlarmParam;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.ResultType;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:47:21
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
	ResultType sendSms(String mobile, String title, String body);

	/**
	 * send weiXin
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	ResultType sendWeiXin(String email, String title, String content);

	/**
	 * send mail
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	ResultType sendMail(String email, String title, String content);

	/**
	 * send sms
	 * 
	 * @param alarm
	 * @return
	 */
	boolean sendSms(Alarm alarm, String receiver);

	/**
	 * weiXin alarm
	 * 
	 * @param alarm
	 * @return
	 */
	boolean sendWeiXin(Alarm alarm, String receiver);

	/**
	 * mail alarm
	 * 
	 * @param emails
	 * @param alarm
	 * @return
	 */
	boolean sendMail(Set<String> emails, Alarm alarm);

	/**
	 * sms alarm
	 * 
	 * @param mobiles
	 * @param alarm
	 * @return
	 */
	boolean sendSms(Set<String> mobiles, Alarm alarm);

	/**
	 * weiXin alarm
	 * 
	 * @param emails
	 * @param alarm
	 * @return
	 */
	boolean sendWeiXin(Set<String> emails, Alarm alarm);

	/**
	 * mail alarm
	 * 
	 * @param alarm
	 * @return
	 */
	boolean sendMail(Alarm alarm, String receiver);

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
	 * find by page
	 * 
	 * @param receiver
	 * @param related
	 * @param subRelated
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
	
	
	long getNextEventId();
}
