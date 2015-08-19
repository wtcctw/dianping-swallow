package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月25日下午12:01:59
 */
public interface MessageDumpService {

	Pair<Long, List<MessageDump>> loadDumpMessagePage(TopicQueryDto topicQueryDto);

	MessageDump loadDumpMessage(String filename);

	int removeDumpMessage(String filename) throws MongoException;

	int updateDumpMessage(MessageDump messageDump);

	ResponseStatus saveDumpMessage(MessageDump messageDump);

	ResponseStatus execDumpMessageTask(MessageDump messageDump);
	
	void execBlockingDumpMessageTask(String topic);

	MessageDump loadUnfinishedDumpMessage(String topic);

	Pair<Long, List<MessageDump>> loadAllDumpMessage();
}
