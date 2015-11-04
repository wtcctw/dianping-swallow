package com.dianping.swallow.common.internal.dao;

import java.util.List;

import com.dianping.swallow.common.internal.message.SwallowMessage;

public interface MessageDAO<T extends Cluster> extends DAO<T>{
	
	public static String METHOD_NAME_GET_MESSAGE = "getMessagesGreaterThan";
	
	/**
	 * 往topic数据库的topicName集合/表里，插入一条信息
	 * 
	 * @param consumerId
	 *            consumerId为null时使用非backup队列
	 * @return
	 */
	void saveMessage(String topicName, String consumerId, SwallowMessage message);

	void saveMessage(String topicName, SwallowMessage message);
	
	void retransmitMessage(String topicName, SwallowMessage message);

	/**
	 * 获取topic数据库的topicName集合/表里，对应messageId字段的记录
	 */
	SwallowMessage getMessage(String topicName, Long messageId);

	/**
	 * 获取topic数据库的topicName集合/表里，size条messageId字段比messageId参数大的记录（按messageId正序排序
	 * ）
	 * 
	 * @param consumerId
	 *            consumerId为null时使用非backup队列
	 */
	List<SwallowMessage> getMessagesGreaterThan(String topicName, String consumerId, Long messageId, int size);

	/**
	 * 获取topic数据库的topicName集合/表里，最大的messageId字段
	 * 
	 * @param topicName
	 * @param consumerId
	 *            consumerId为null时使用非backup队列
	 * @return
	 */
	Long getMaxMessageId(String topicName, String consumerId);

	Long getMaxMessageId(String topicName);

	/**
	 * 获取topic数据库的topicName集合/表里，messageId字段最大的消息
	 * 
	 * @param topicName
	 * @return
	 */
	SwallowMessage getMaxMessage(String topicName);

	/**
	 * 清理所有消息
	 * 
	 * @param topicName
	 * @param consumerId
	 */
	void cleanMessage(String topicName, String consumerId);

	/**
	 * 删除messageId之前的消息
	 * 
	 * @param topicName
	 * @param messageId
	 */
	int deleteMessage(String topicName, Long messageId);

	/**
	 * 删除messageId之前的消息
	 * 
	 * @param topicName
	 * @param consumerId
	 * @param messageId
	 */
	int deleteMessage(String topicName, String consumerId, Long messageId);

	/**
	 * 获取消息数目
	 * @return
	 */
	int count(String topicName, String consumerId);
	
	/**
	 * 获取当前consumerId还有多少堆积消息
	 * @return
	 */
	long getAccumulation(String topicName, String consumerId);


	/*****************************************
	 * Ack 相关函数
	 * ***************************************/
	
	void cleanAck(String topicName, String consumerId, boolean isBackup);
	
	void cleanAck(String topicName, String consumerId);

   /**
    * 获取topicName和consumerId对应的最大的messageId
    */
   Long getAckMaxMessageId(String topicName, String consumerId, boolean isBackup);

   Long getAckMaxMessageId(String topicName, String consumerId);

   /**
    * 添加一条topicName，consumerId，messageId记录
    */
   void addAck(String topicName, String consumerId, Long messageId, String desc, boolean isBackup);

   void addAck(String topicName, String consumerId, Long messageId, String desc);

}
