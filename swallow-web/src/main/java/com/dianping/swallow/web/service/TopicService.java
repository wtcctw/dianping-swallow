package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.Topic;


/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:57
 */
public interface TopicService extends ConfigChange{
	
	/**
	 * 查询出限定个数的topic
	 * @param start 起始位置
	 * @param span	偏移量
	 */
	Pair<Long, List<Topic>> loadTopicPage(TopicQueryDto topicQueryDto);

	/**
	 * 根据消息名称，部门，申请人查询消息
	 * @param start 起始位置
	 * @param span  偏移量
	 * @param name  topic名称
	 * @param proposal  申请人
	 */
	Pair<Long, List<Topic>> loadSpecificTopicPage(TopicQueryDto topicQueryDto);
	
	/**
	 * 
	 * @param name  topic名称
	 */
	Topic loadTopicByName(String name);


	/**
	 * 编辑topic信息
	 * @param name topic名称
	 * @param proposal 申请人
	 * @param dept 申请人部门
	 * @param time 申请时间
	 */
	int editTopic(String name, String proposal, String time);

	/**
	 * 返回所有topic名称
	 * @param username  用户名
	 * @param isAdmin  		 是否是管理员
	 */
	List<String> loadTopicNames(String username, boolean isAdmin);

	/**
	 * 
	 * @param username  通行证
	 * @param all       是否返回所有
	 */
	Pair<List<String>, List<String>> loadTopicProposal(String username, boolean isAdmin);
	
	/**
	 * 
	 * @param topic topic实例
	 */
	int saveTopic(Topic topic);
	
	List<String> loadTopicNames(String username);

	Map<String, Set<String>> loadCachedTopicToWhiteList();

}
