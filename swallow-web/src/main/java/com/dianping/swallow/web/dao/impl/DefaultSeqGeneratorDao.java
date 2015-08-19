package com.dianping.swallow.web.dao.impl;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.SeqGeneratorDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * 
 * @author qiyin
 *
 */
@Service("seqGeneratorDao")
public class DefaultSeqGeneratorDao extends AbstractWriteDao implements SeqGeneratorDao {

	private static final String SEQGENERATOR_COLLECTION = "SEQ_GENERATOR";

	private final static String CATEGORY_FIELD = "category";
	private static final String SEQ_FIELD = "id";

	@Override
	public long nextSeq(String category) {
		try {
			DBObject update = new BasicDBObject("$inc", new BasicDBObject(SEQ_FIELD, 1L));
			DBObject query = new BasicDBObject(CATEGORY_FIELD, category);
			return (Long) mongoTemplate.getCollection(SEQGENERATOR_COLLECTION)
					.findAndModify(query, null, null, false, update, true, true).get(SEQ_FIELD);
		} catch (MongoException e) {
			logger.error("[nextSeq] mongo handle error.", e);
			return 0L;
		}
	}
}
