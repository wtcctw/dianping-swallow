package com.dianping.swallow.web.dao;

import java.text.ParseException;
import java.util.List;
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
	 * @param offset
	 * @param limit
	 * @param ip
	 * @return
	 */
	Map<String, Object> findByIp(int offset, int limit, String ip, String topicName);

	/**
	 * @param p
	 */
	void create(WebSwallowMessage p, String topicName);

	/**
	 * @param id
	 * @return
	 */
	WebSwallowMessage readById(long id, String topicName);

	/**
	 * @param p
	 */
	void update(WebSwallowMessage p, String topicName);

	/**
	 * @param id
	 * @return
	 */
	int deleteById(String id, String topicName);

	/**
	 * @return
	 */
	long count(String topicName);

	/**
	 * @param offset
	 * @param limit
	 * @param mid
	 * @return
	 */
	List<WebSwallowMessage> findSpecific(int offset, int limit, long mid, String topicName);

	/**
	 * @param offset
	 * @param limit
	 * @param startdt
	 * @param stopdt
	 * @return
	 * @throws ParseException 
	 */
	Map<String, Object> findByTime(int offset, int limit, String startdt,
			String stopdt, String topicName);
	
	Map<String, Object> findByTimeAndId(int offset, int limit, long mid, String startdt, String stopdt, String topicName);
	
	Map<String, Object> findByTopicname(int offset, int limit, String topicName);
	
}
