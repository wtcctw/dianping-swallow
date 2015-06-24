package com.dianping.swallow.web.dao.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.dao.MessageDumpDao;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;

/**
 * @author mingdongli
 *
 * 2015年6月24日下午1:57:22
 */
public class DefaultMessageDumpDao extends AbstractWriteDao implements MessageDumpDao {
	
	private static final String MESSAGEDUMP_COLLECTION = "swallowwebmessagedumpc";

	private static final String TOPIC = "topic";
	private static final String SIZE = "size";
	private static final String MESSAGE = "message";

	@Override
	public int saveMessageDump(MessageDump mdump) {
		try {
			mongoTemplate.save(mdump, MESSAGEDUMP_COLLECTION);
			return ResponseStatus.SUCCESS.getStatus();
		} catch (MongoSocketException e) {
			logger.error(e.getMessage(), e);
			return ResponseStatus.TRY_MONGOWRITE.getStatus();
		} catch (MongoException e) {
			logger.error("Error when save topic " + mdump, e);
		}
		return ResponseStatus.MONGOWRITE.getStatus();
	}

	@Override
	public Map<String, Object> loadMessageDump(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		List<MessageDump> messageDumpList = mongoTemplate.find(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
		Long messageDumpSize = this.countMessageDump();
		return getResponse(messageDumpSize, messageDumpList);
	}

	@Override
	public Map<String, Object> loadSpecifitMessageDump(int offset, int limit, String topic) {
		List<MessageDump> messageDumpList = new ArrayList<MessageDump>();

		Query query1 = new Query(Criteria.where(TOPIC).is(topic));
		Query query2 = query1;
		query1.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		messageDumpList = mongoTemplate.find(query1, MessageDump.class, MESSAGEDUMP_COLLECTION);
		Long messageDumpSize = mongoTemplate.count(query2, MESSAGEDUMP_COLLECTION);
		return getResponse(messageDumpSize, messageDumpList);
	}
	
	@Override
	public long countMessageDump() {
		Query query = new Query();
		return mongoTemplate.count(query, MESSAGEDUMP_COLLECTION);
	}
	
	private Map<String, Object> getResponse(Long messageDumpSize, List<MessageDump> messageDumpList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, messageDumpSize);
		map.put(MESSAGE, messageDumpList);
		return map;
	}

}
