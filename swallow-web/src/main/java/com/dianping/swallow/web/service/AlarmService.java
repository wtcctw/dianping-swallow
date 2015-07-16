package com.dianping.swallow.web.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmLevelType;

/**
 *
 * @author qiyin
 * 
 */
public interface AlarmService {
	/**
	 * sms alarm
	 * 
	 * @param mobile
	 * @param title
	 * @param body
	 * @param type
	 * @return
	 */
	public boolean sendSms(String mobile, String title, String body, AlarmLevelType type);

	/**
	 * weiXin alarm
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendWeiXin(String email, String title, String content, AlarmLevelType type);

	/**
	 * mail alarm
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendMail(Set<String> emails, String title, String content, AlarmLevelType type);
	
	/**
	 * sms alarm
	 * 
	 * @param mobile
	 * @param title
	 * @param body
	 * @param type
	 * @return
	 */
	public boolean sendSms(Set<String> mobiles, String title, String body, AlarmLevelType type);

	/**
	 * weiXin alarm
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendWeiXin(Set<String> emails, String title, String content, AlarmLevelType type);

	/**
	 * mail alarm
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendMail(String email, String title, String content, AlarmLevelType type);

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
