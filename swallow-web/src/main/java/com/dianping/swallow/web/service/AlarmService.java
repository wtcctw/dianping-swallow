package com.dianping.swallow.web.service;

/**
 *
 *@author qiyin 
 * 
 */
public interface AlarmService  extends SwallowService {
	/**
	 * sms alarm
	 * @param mobile
	 * @param body
	 * @return
	 */
	public boolean sendSms(String mobile,String body);
	/**
	 * weiXin alarm
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
	/**
	 * send sms weiXin mail
	 * @param ip
	 * @param title
	 * @param message
	 */
	public void sendAll(String ip,String title,String message);

}
