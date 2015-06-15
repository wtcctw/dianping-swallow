package com.dianping.swallow.web.dao;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.web.model.Topic;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:13
 */
public interface TopicDao extends Dao {
	
	/**
	 * 根据topic名称查询topic
	 * @param name  topic名称
	 */
    Topic readByName(String name) ;

    /**
     * 保存topic
     * @param topic 
     */
	int saveTopic(Topic topic);
	
	/**
	 * 更新topic
	 * @param name  topic名称
	 * @param prop  申请人
	 * @param dept  申请人部门
	 * @param time  申请时间
	 */
	int updateTopic(String name, String prop, String time);

	/**
	 *  删除集合
	 */
	void dropCol();
	
	/**
	 * 查询所有纪录 
	 */
	List<Topic> findAll();
	
	/**
	 * 查询纪录个数 
	 */
	long countTopic();
	
	/**
	 * 查询指定数目的topic
	 * @param offset
	 * @param limit
	 */
	Map<String, Object> findFixedTopic(int offset, int limit);
	
	/**
	 * 在限定条件下查询特定的topic
	 * @param offset  起始位置
	 * @param limit   偏移量
	 * @param name    topic名称
	 * @param prop    申请人
	 * @param dept	  申请人部门
	 */
	Map<String, Object> findSpecific(int offset, int limit, String name, String prop);
	
	
	/**
	 * 根据申请人查询topic
	 * @param prop
	 */
	Topic readByProp(String prop);
	
 }