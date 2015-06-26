package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.web.model.MessageDump;
import com.mongodb.MongoException;


/**
 * @author mingdongli
 *
 * 2015年6月25日下午12:01:59
 */
public interface MessageDumpService {
	
	Map<String, Object> loadSpecificDumpMessage(int start, int span, String topic);

	MessageDump loadDumpMessage(String filename);

	List<MessageDump> loadAllDumpMessage();

	int removeDumpMessage(String filename) throws MongoException;
	
	int saveDumpMessage(String topic, String name, String startdt, String stopdt, String filename, boolean finished);
	
	int updateDumpMessageStatus(String filename, boolean finished);
	
}
