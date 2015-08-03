package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月25日下午12:01:59
 */
public interface MessageDumpService {

	/**
	 * 
	 * @param start 开始
	 * @param span  偏移量
	 * @param topic topic名称
	 */
	Pair<Long, List<MessageDump>> loadDumpMessagePage(int start, int span, String topic);

	/**
	 * 
	 * @param topic topic名称
	 */
	MessageDump loadUnfinishedDumpMessage(String topic);

	/**
	 * 
	 * @param filename 文件名称
	 */
	MessageDump loadDumpMessage(String filename);

	Pair<Long, List<MessageDump>> loadAllDumpMessage();

	/**
	 * 
	 * @param filename  文件名称
	 * @throws MongoException
	 */
	int removeDumpMessage(String filename) throws MongoException;

	int updateDumpMessage(String filename, boolean finished, int totalcount, int size, String firsttime, String lasttime);

	ResponseStatus saveDumpMessage(String topic, String name, String startdt, String stopdt, String filename, boolean finished);

	ResponseStatus execDumpMessageTask(String topic, String startdt, String stopdt, String filename, String username);
	
	void execBlockingDumpMessageTask(String topic);
}
