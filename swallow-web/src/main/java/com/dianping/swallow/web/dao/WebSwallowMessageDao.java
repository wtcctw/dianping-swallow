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
	

	Map<String, Object> findByIp(int offset, int limit, String ip, String topicName);

	void create(WebSwallowMessage p, String topicName);

	WebSwallowMessage readById(long id, String topicName);

	void update(WebSwallowMessage p, String topicName);

	int deleteById(String id, String topicName);

	long count(String topicName);

	Map<String, Object> findSpecific(int offset, int limit, long mid, String topicName);

	Map<String, Object> findByTime(int offset, int limit, String startdt,
			String stopdt, String topicName);
	
	Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt, String topicName);
	
	Map<String, Object> findByTopicname(int offset, int limit, String topicName);
	
}
