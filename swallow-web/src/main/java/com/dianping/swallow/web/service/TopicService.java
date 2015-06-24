package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.web.model.Topic;


/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:57
 */
public interface TopicService {
	
	/**
	 * 查询出限定个数的topic
	 * @param start 起始位置
	 * @param span	偏移量
	 */
	Map<String, Object> loadAllTopic(int start, int span);

	/**
	 * 根据消息名称，部门，申请人查询消息
	 * @param start 起始位置
	 * @param span  偏移量
	 * @param name  topic名称
	 * @param prop  申请人
	 * @param dept  申请人部门
	 */
	Map<String, Object> loadSpecificTopic(int start, int span, String name,
			String prop);
	
	/**
	 * 
	 * @param name  topic名称
	 */
	Topic loadTopic(String name);


	/**
	 * 编辑topic信息
	 * @param name topic名称
	 * @param prop 申请人
	 * @param dept 申请人部门
	 * @param time 申请时间
	 */
	int editTopic(String name, String prop, String time);

	/**
	 * 返回所有topic名称
	 * @param tongXingZheng  用户名
	 * @param isAdmin  		 是否是管理员
	 */
	List<String> loadAllTopicNames(String username, boolean isAdmin);

	/**
	 * 
	 * @param username  通行证
	 * @param all       是否返回所有
	 */
	Map<String, Object[]> getPropAndDept(String username, boolean all);
	
	/**
	 * 
	 * @param topic topic实例
	 */
	int saveTopic(Topic topic);
	
	Map<String, Set<String>> loadTopicToWhiteList();
	

}
