package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Topic;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:13
 */
public interface TopicDao extends Dao {

	Topic readById(String id);
	
    Topic readByName(String name) ;

	void saveTopic(Topic p);
	
	void updateTopic(String name, String person, String dept, String time);

	int deleteById(String id);
	
	void dropCol();
	
	List<Topic> findAll();
	
	long countTopic();
	
	List<Topic> findFixedTopic(int offset, int limit);
	
	List<Topic> findSpecific(String name, String prop, String dept);
	
 }