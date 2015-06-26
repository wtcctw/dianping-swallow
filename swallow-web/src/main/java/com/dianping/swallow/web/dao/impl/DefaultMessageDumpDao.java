package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.MessageDumpDao;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年6月24日下午1:57:22
 */
@Component
public class DefaultMessageDumpDao extends AbstractWriteDao implements MessageDumpDao {

	private static final String MESSAGEDUMP_COLLECTION = "swallowwebmessagedumpc";

	private static final String TOPIC = "topic";
	private static final String SIZE = "size";
	private static final String MESSAGE = "message";
	private static final String FILENAME = "filename";
	private static final String FINISHED = "finished";

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
	public Map<String, Object> loadMessageDumps(int offset, int limit) {
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

	@Override
	public void dropCol() {

		mongoTemplate.dropCollection(MESSAGEDUMP_COLLECTION);
	}

	@Override
	public MessageDump loadMessageDump(String filename) {

		Query query = new Query(Criteria.where(FILENAME).is(filename));
		return mongoTemplate.findOne(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
	}

	@Override
	public int updateMessageDumpStatus(String filename, boolean finished) throws MongoException {

		Query query = new Query(Criteria.where(FILENAME).is(filename));

		Update update = new Update();
		update.set(FINISHED, finished);

		WriteResult wr = mongoTemplate.upsert(query, update, MESSAGEDUMP_COLLECTION);
		return wr.getN();

	}

	@Override
	public int removeMessageDump(String filename) throws MongoException{

		Query query = new Query(Criteria.where(FILENAME).is(filename));
		WriteResult result = mongoTemplate.remove(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
		return result.getN();
	}

	@Override
	public List<MessageDump> loadAllMessageDumps() {
		
		return mongoTemplate.findAll(MessageDump.class, MESSAGEDUMP_COLLECTION);
	}

}
