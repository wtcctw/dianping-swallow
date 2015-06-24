package com.dianping.swallow.web.dao;

import java.util.Map;

import com.dianping.swallow.web.model.MessageDump;


/**
 * @author mingdongli
 *
 * 2015年6月24日下午1:42:29
 */
public interface MessageDumpDao extends Dao{

	long countMessageDump();
	
	int saveMessageDump(MessageDump mdump);
	
	Map<String, Object> loadMessageDump(int offset, int limit);

	Map<String, Object> loadSpecifitMessageDump(int offset, int limit, String topic);
}
