package com.dianping.swallow.common.internal.dao.impl.mongodb;


import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDao;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author mengwenchao
 *
 * 2015年3月26日 下午1:47:15
 */
public abstract class AbstractMongoMessageDao extends AbstractMessageDao<MongoCluster> implements MessageDAO<MongoCluster>{


	private static final long serialVersionUID = 1L;

	protected static final String gt = "$gt";
	protected static final String gte = "$gte";
	protected static final String lt = "$lt";
	protected static final String lte = "$lte";
	protected static final String ne = "$ne";

	public AbstractMongoMessageDao(MongoCluster cluster) {
		super(cluster);
	}
	

	protected WriteResult doAndCheckResult(MongoAction mongoAction) throws SwallowMongoException {

		WriteResult result = mongoAction.doAction();
		@SuppressWarnings("deprecation")
		String error = result.getError(); 
		if(error != null){
			throw new SwallowMongoException(error);
		}
		return result;
	}
	
	
	protected static interface MongoAction{
		WriteResult doAction();
	}
	
	protected static class Query{
		
		private DBObject query;
		
		public Query(){
			query = new BasicDBObject();
		}
		
		public Query lt(String field, Object condition){
			query.put(field, buildCondition(lt, condition));
			return this;
		}
		
		public Query lte(String field, Object condition){
			query.put(field, buildCondition(lte, condition));
			return this;
		}

		public Query gt(String field, Object condition){
			query.put(field, buildCondition(gt, condition));
			return this;
		}
		
		public Query gte(String field, Object condition){
			query.put(field, buildCondition(gte, condition));
			return this;
		}

		private Object buildCondition(String key, Object condition) {
			return BasicDBObjectBuilder.start(key, condition).get();
		}

		DBObject build(){
			return query;
		}
	}

}
