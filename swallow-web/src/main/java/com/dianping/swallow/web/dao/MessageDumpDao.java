package com.dianping.swallow.web.dao;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.web.model.MessageDump;
import com.mongodb.MongoException;


/**
 * @author mingdongli
 *
 * 2015年6月24日下午1:42:29
 */
public interface MessageDumpDao extends Dao{
	
	void dropCol();

	long countMessageDump();
	
	int saveMessageDump(MessageDump mdump);
	
	int removeMessageDump(String filename) throws MongoException;
	
	int updateMessageDumpStatus(String filename, boolean finished) throws MongoException;
	
	List<MessageDump> loadAllMessageDumps();

	Map<String, Object> loadMessageDumps(int offset, int limit);

	MessageDump loadMessageDump(String filename);

	Map<String, Object> loadSpecifitMessageDump(int offset, int limit, String topic);
}
