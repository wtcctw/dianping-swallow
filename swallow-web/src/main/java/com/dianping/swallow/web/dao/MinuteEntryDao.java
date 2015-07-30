package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.dashboard.Entry;


/**
 * @author mingdongli
 *
 * 2015年7月27日下午4:19:40
 */
public interface EntryDao extends Dao {

	/**
	 * 
	 * @param entryDao
	 */
	boolean insert(Entry entry);

	/**
	 * 
	 * @param topic
	 */
	Pair<Integer, List<EntryDao>> findByTopic(String topic);
	
	/**
	 * 
	 * @param cid
	 */
	Pair<Integer, List<EntryDao>> findByConsumerId(String cid);

	/**
	 * 
	 * @param offset
	 * @param limit
	 */
	Pair<Integer, List<EntryDao>> loadEntryPage(int offset, int limit);

}

