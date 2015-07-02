package com.dianping.swallow.web.service;

/**
 *
 *@author qiyin 
 * 
 */
public interface AlarmService {
	/**
	 * 短信告警
	 * @param mobile
	 * @param body
	 * @return
	 */
	public boolean sendSms(String mobile,String body);
	/**
	 * 微信告警
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendWeixin(String email,String title,String content);
	/**
	 * 邮件告警
	 * @param email
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean sendMail(String email,String title,String content);
	

}
