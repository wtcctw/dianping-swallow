package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
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
	private static final String FILENAME = "filename";
	private static final String FINISHED = "finished";
	private static final String DESC = "desc";
	private static final String TIME = "time";

	@Override
	public ResponseStatus saveMessageDump(MessageDump mdump) {
		
		try {
			mongoTemplate.save(mdump, MESSAGEDUMP_COLLECTION);
			return ResponseStatus.SUCCESS;
		} catch (MongoSocketException e) {
			logger.error(e.getMessage(), e);
			return ResponseStatus.TRY_MONGOWRITE;
		} catch (MongoException e) {
			logger.error("Error when save message dump " + mdump, e);
		}
		return ResponseStatus.MONGOWRITE;
	}

	@Override
	public Pair<Long, List<MessageDump>> loadMessageDumpPage(int offset, int limit) {
		
		Query query = new Query();
		
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		List<MessageDump> messageDumpList = mongoTemplate.find(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
		
		Long messageDumpSize = this.count();
		return new Pair<Long, List<MessageDump>>(messageDumpSize, messageDumpList);
	}

	@Override
	public Pair<Long, List<MessageDump>> loadMessageDumpPageByTopic(int offset, int limit, String topic) {
		List<MessageDump> messageDumpList = new ArrayList<MessageDump>();

		String[] topics = topic.split(",");

		List<Criteria> criterias = new ArrayList<Criteria>();
		for (String t : topics) {
			criterias.add(Criteria.where(TOPIC).is(t));
		}

		Query query1 = new Query();
		query1.addCriteria(Criteria.where(TOPIC).exists(true)
				.orOperator(criterias.toArray(new Criteria[criterias.size()])));

		query1.skip(offset)
				.limit(limit)
				.with(new Sort(new Sort.Order(Direction.DESC, FINISHED), new Sort.Order(Direction.ASC, TOPIC),
						new Sort.Order(Direction.DESC, TIME)));
		messageDumpList = mongoTemplate.find(query1, MessageDump.class, MESSAGEDUMP_COLLECTION);
		Long messageDumpSize = mongoTemplate.count(query1, MESSAGEDUMP_COLLECTION);
		
		return new Pair<Long, List<MessageDump>>(messageDumpSize, messageDumpList);
	}

	@Override
	public long count() {
		Query query = new Query();
		return mongoTemplate.count(query, MESSAGEDUMP_COLLECTION);
	}

	@Override
	public MessageDump loadMessageDump(String filename) {

		Query query = new Query(Criteria.where(FILENAME).is(filename));
		return mongoTemplate.findOne(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
	}

	@Override
	public int updateMessageDumpStatus(String filename, boolean finished, String desc) throws MongoException {

		Query query = new Query(Criteria.where(FILENAME).is(filename));

		Update update = new Update();
		update.set(FINISHED, finished);
		update.set(DESC, desc);

		WriteResult wr = mongoTemplate.upsert(query, update, MESSAGEDUMP_COLLECTION);
		return wr.getN();

	}

	@Override
	public int removeMessageDump(String filename) throws MongoException {

		Query query = new Query(Criteria.where(FILENAME).is(filename));
		WriteResult result = mongoTemplate.remove(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
		return result.getN();
	}

	@Override
	public Pair<Long, List<MessageDump>> loadAllMessageDumps() {

		List<MessageDump> messageDumpList = mongoTemplate.findAll(MessageDump.class, MESSAGEDUMP_COLLECTION);
		Long size = (long) messageDumpList.size();
		return new Pair<Long, List<MessageDump>>(size, messageDumpList);
	}

	@Override
	public MessageDump loadUnfinishedMessageDump(String topic) {

		Query query = new Query(new Criteria().andOperator(Criteria.where(TOPIC).is(topic), Criteria.where(FINISHED)
				.is(false)));
		return mongoTemplate.findOne(query, MessageDump.class, MESSAGEDUMP_COLLECTION);
	}

}
