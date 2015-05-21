package com.dianping.swallow.web.dao;

import java.util.Map;

import com.dianping.swallow.web.dao.impl.WebMongoManager;
import com.dianping.swallow.web.model.WebSwallowMessage;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:31
 */
public interface WebSwallowMessageDao extends Dao, WebMongoManager{
	
	/**
	 * 根据IP查找消息
	 * @param offset  	起始位置
	 * @param limit   	偏移量
	 * @param ip      	IP
	 * @param topicName topic名称
	 */
	Map<String, Object> findByIp(int offset, int limit, String ip, String topicName);

	/**
	 * 新建WebSwallowMessage
	 * @param wsm         WebSwallowMessage
	 * @param topicName	  topic名称
	 */
	void create(WebSwallowMessage wsm, String topicName);

	/**
	 * 根据消息ID查询
	 * @param mid	  	消息ID
	 * @param topicName	topic名称
	 */
	WebSwallowMessage readById(long mid, String topicName);
	
	/**
	 * 
	 * @param wsm		WebSwallowMessage
	 * @param topicName topic名称
	 */
	void update(WebSwallowMessage wsm, String topicName);

	/**
	 * 根据消息ID删除message
	 * @param mid	    消息ID
	 * @param topicName topic名称
	 */
	int deleteById(String mid, String topicName);

	/**
	 * 查询topic下messages个数
	 * @param topicName
	 */
	long count(String topicName);

	/**
	 * 根据ID查询messages
	 * @param offset	起始位置
	 * @param limit		偏移量
	 * @param mid		消息ID
	 * @param topicName	消息名称
	 */
	Map<String, Object> findSpecific(int offset, int limit, long mid, String topicName);

	/**
	 * 根据时间查询messags
	 * @param offset	起始位置
	 * @param limit     偏移量
	 * @param startdt	开始时间
	 * @param stopdt	结束时间
	 * @param topicName	消息名称
	 */
	Map<String, Object> findByTime(int offset, int limit, String startdt,
			String stopdt, String topicName);
	
	/**
	 * 根据时间和消息ID查询messages
	 * @param offset	 起始位置
	 * @param limit		 偏移量
	 * @param mid		 消息ID
	 * @param startdt    开始时间
	 * @param stopdt     结束时间
	 * @param topicName  消息名称
	 */
	Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt, String topicName);
	
	/**
	 * 根据topic名称查询messages
	 * @param offset     起始位置
	 * @param limit      偏移量
	 * @param topicName  topic名称
	 */
	Map<String, Object> findByTopicname(int offset, int limit, String topicName);
	
}
