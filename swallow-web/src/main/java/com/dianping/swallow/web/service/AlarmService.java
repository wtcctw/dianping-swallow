package com.dianping.swallow.web.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.alarm.Alarm;

/**
 *
 * @author qiyin
 * 
 */
public interface AlarmService {
	/**
	 * send sms
	 * @param alarm
	 * @return
	 */
	public boolean sendSms(Alarm alarm);

	/**
	 * weiXin alarm
	 * 
	 * @param alarm
	 * @return
	 */
	public boolean sendWeiXin(Alarm alarm);

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
	public boolean sendMail(Alarm alarm);

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
	 * find by id
	 * 
	 * @param ipDesc
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
