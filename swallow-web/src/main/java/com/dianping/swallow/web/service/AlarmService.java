package com.dianping.swallow.web.service;

/**
 *
 *@author qiyin 
 * 
 */
public interface AlarmService {
	/**
	 * sms alarm
	 * @param mobile
	 * @param body
	 * @return
	 */
	public boolean sendSms(String mobile,String body);
	/**
	 * wei alarm
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendWeixin(String email,String title,String content);
	/**
	 * mail alarm
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendMail(String email,String title,String content);

}
