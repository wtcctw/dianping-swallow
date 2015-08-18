package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;


/**
 * @author mingdongli
 *
 * 2015年6月24日下午1:42:29
 */
public interface MessageDumpDao extends Dao{
	
	long count();
	
	int updateMessageDump(MessageDump mdump) throws MongoException;

	int removeMessageDump(String filename) throws MongoException;

	ResponseStatus saveMessageDump(MessageDump mdump);
	
	MessageDump loadUnfinishedMessageDump(String topic);

	MessageDump loadMessageDump(String filename);

	Pair<Long, List<MessageDump>> loadAllMessageDumps();

	Pair<Long, List<MessageDump>> loadMessageDumpPage(int offset, int limit);

	Pair<Long, List<MessageDump>> loadMessageDumpPageByTopic(TopicQueryDto topicQueryDto);
}
