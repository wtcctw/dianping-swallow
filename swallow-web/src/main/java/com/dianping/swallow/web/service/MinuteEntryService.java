package com.dianping.swallow.web.service;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.dashboard.model.MinuteEntry;
import com.dianping.swallow.web.dashboard.model.ResultEntry;


/**
 * @author mingdongli
 *
 * 2015年7月29日上午11:48:56
 */
public interface MinuteEntryService {

	/**
	 * 
	 * @param entry
	 */
	int insert(MinuteEntry entry);

	/**
	 * 
	 * @param start 起始时间
	 * @param stop 结束时间
	 */
	List<ResultEntry> loadMinuteEntryPage(Date start, Date stop, String type);
}
