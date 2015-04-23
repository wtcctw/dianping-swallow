package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Topic;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:13
 */
public interface TopicDAO  {

	public void create(Topic p);

	public Topic readById(String id);
	
    public Topic readByName(String name) ;

	public void saveTopic(Topic p);
	
	public void updateTopic(String name, String person, String dept, String time);

	public int deleteById(String id);
	
	public void dropCol();
	
	public List<Topic> findAll();
	
	public long countTopic();
	
	public List<Topic> findFixedTopic(int offset, int limit);
	
	//each topic have one record in database
	public List<Topic> findSpecific(String name, String prop, String dept);
	
 }