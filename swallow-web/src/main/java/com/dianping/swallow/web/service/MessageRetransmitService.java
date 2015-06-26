package com.dianping.swallow.web.service;


/**
 * @author mingdongli
 *
 * 2015年5月20日下午6:17:30
 */
public interface MessageRetransmitService {

	/**
	 * 重发已经保存的消息
	 * @param topicName	topic名称
	 * @param mid		消息ID
	 */
	boolean doRetransmit(String topicName, long mid);
	
	/**
	 * 发送自定义消息
	 * @param topicName	topic名称
	 * @param content	消息内容
	 * @param type	    消息类型
	 * @param delimitor	属性分隔符
	 * @param property	消息属性
	 */
	void saveNewMessage(String topicName, String content, String type, String delimitor, String property);
	
}
