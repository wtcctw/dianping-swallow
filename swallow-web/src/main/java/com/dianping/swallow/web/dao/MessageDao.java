package com.dianping.swallow.web.dao;

import java.util.Map;

import com.dianping.swallow.web.model.Message;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:05:31
 */
public interface MessageDao extends Dao {

	/**
	 * 根据IP查找消息
	 * 
	 * @param offset
	 *            起始位置
	 * @param limit
	 *            偏移量
	 * @param ip
	 *            IP
	 * @param topicName
	 *            topic名称
	 */
	Map<String, Object> findByIp(int offset, int limit, String ip, String topicName);

	/**
	 * 新建WebSwallowMessage
	 * 
	 * @param wsm
	 *            WebSwallowMessage
	 * @param topicName
	 *            topic名称
	 */
	boolean createMessage(Message wsm, String topicName);

	/**
	 * 根据消息ID查询
	 * 
	 * @param mid
	 *            消息ID
	 * @param topicName
	 *            topic名称
	 */
	Message readById(long mid, String topicName);

	/**
	 * 
	 * @param wsm
	 *            WebSwallowMessage
	 * @param topicName
	 *            topic名称
	 */
	boolean update(Message wsm, String topicName);

	/**
	 * 根据消息ID删除message
	 * 
	 * @param mid
	 *            消息ID
	 * @param topicName
	 *            topic名称
	 */
	int deleteById(String mid, String topicName);

	/**
	 * 查询topic下messages个数
	 * 
	 * @param topicName
	 */
	long count(String topicName);

	/**
	 * 根据ID查询messages
	 * 
	 * @param offset
	 *            起始位置
	 * @param limit
	 *            偏移量
	 * @param mid
	 *            消息ID
	 * @param topicName
	 *            消息名称
	 */
	Map<String, Object> findSpecific(int offset, int limit, long mid, String topicName, boolean sort);

	/**
	 * 根据时间查询messags
	 * 
	 * @param offset
	 *            起始位置
	 * @param limit
	 *            偏移量
	 * @param startdt
	 *            开始时间
	 * @param stopdt
	 *            结束时间
	 * @param topicName
	 *            消息名称
	 * @param basemid
	 *            消息ID查询基准值
	 */
	Map<String, Object> findByTime(int offset, int limit, String startdt, String stopdt, String topicName,
			String basemid, boolean sort);

	/**
	 * 根据时间和消息ID查询messages
	 * 
	 * @param offset
	 *            起始位置
	 * @param limit
	 *            偏移量
	 * @param mid
	 *            消息ID
	 * @param startdt
	 *            开始时间
	 * @param stopdt
	 *            结束时间
	 * @param topicName
	 *            消息名称
	 */
	Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt, String topicName);

	/**
	 * 根据topic名称查询messages
	 * 
	 * @param offset
	 *            起始位置
	 * @param limit
	 *            偏移量
	 * @param topicName
	 *            topic名称
	 * @param baseMid
	 *            消息ID查询基准值
	 */
	Map<String, Object> findByTopicname(int offset, int limit, String topicName, String baseMid, boolean sort);

	/**
	 * 
	 * @param topicName
	 */
	Message loadFirstMessage(String topicName);

	/**
	 * 
	 * @param topicName
	 *            topic名称
	 * @param startdt
	 *            开始时间
	 * @param stopdt
	 *            结束时间
	 * @param filename
	 *            保存文件名
	 */
	Map<String, Object> exportMessages(String topicName, String startdt, String stopdt);

}
