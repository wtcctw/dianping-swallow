package com.dianping.swallow.web.service;

import java.util.Map;

import com.dianping.swallow.web.model.Message;


/**
 * @author mingdongli
 *
 * 2015年5月14日下午1:16:39
 */
public interface MessageService {

	
	/**
	 * 在限定条件下查询topic指定数量的messages
	 * @param start  	开始位置
	 * @param span	 	偏移量
	 * @param tname     topic名称
	 * @param messageId 消息ID
	 * @param startdt   开始时间
	 * @param stopdt	结束时间	
	 * @param username  用户名
	 * @param baseMid   基准消息ID
	 */
	Map<String, Object> getMessageFromSpecificTopic(int start, int span, String tname, 
			String messageId, String startdt, String stopdt, String username, String baseMid);
	
	/**
	 * 查询指定消息ID的消息内容
	 * @param topic 消息名称
	 * @param mid	消息ID
	 */
	Message getMessageContent(String topic, String mid);
	

	/**
	 * 
	 * @param topicName
	 */
	Map<String, Object> loadMinAndMaxTime(String topicName);
	
	/**
	 * 
	 * @param topicName  topic名称
	 * @param startdt    开始时间
	 * @param stopdt     结束时间
	 * @param filename   保存文件名
	 */
	Integer exportMessage(String topicName, String startdt, String stopdt, String filename);
	
}
