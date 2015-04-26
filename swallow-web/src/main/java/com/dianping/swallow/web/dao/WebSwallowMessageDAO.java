package com.dianping.swallow.web.dao;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.bson.types.BSONTimestamp;

import com.dianping.swallow.web.model.WebSwallowMessage;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:31
 */
public interface WebSwallowMessageDAO extends Dao{
	


	/**
	 * @param offset
	 * @param limit
	 * @param ip
	 * @return
	 */
	Map<String, Object> findByIp(int offset, int limit, String ip);

	/**
	 * @param p
	 */
	void create(WebSwallowMessage p);

	/**
	 * @param id
	 * @return
	 */
	WebSwallowMessage readById(BSONTimestamp id);

	/**
	 * @param p
	 */
	void update(WebSwallowMessage p);

	/**
	 * @param id
	 * @return
	 */
	int deleteById(String id);

	/**
	 * @return
	 */
	long count();

	/**
	 * @param offset
	 * @param limit
	 * @param mid
	 * @return
	 */
	List<WebSwallowMessage> findSpecific(int offset, int limit, long mid);

	/**
	 * @param offset
	 * @param limit
	 * @param startdt
	 * @param stopdt
	 * @return
	 * @throws ParseException 
	 */
	Map<String, Object> findByTime(int offset, int limit, String startdt,
			String stopdt);
	
}
