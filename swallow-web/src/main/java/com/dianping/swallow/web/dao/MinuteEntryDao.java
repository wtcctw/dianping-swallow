package com.dianping.swallow.web.dao;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.model.dashboard.MinuteEntry;


/**
 * @author mingdongli
 *
 * 2015年7月27日下午4:19:40
 */
public interface MinuteEntryDao extends Dao {
	
	/**
	 * 
	 * @param entry
	 */
	int insert(MinuteEntry entry);


	/**
	 * 
	 * @param start  起始时间
	 * @param stop   结束时间
	 * @param limit  文档数
	 */
	List<MinuteEntry> loadMinuteEntryPage(Date start, Date stop, int limit);

}

