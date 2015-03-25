package com.dianping.swallow.test.mongo;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author mengwenchao
 *
 * 2015年3月23日 下午3:55:24
 */
public class MongoCollectionTest extends AbstractMongoTest{
	
	
	private String databaseName = "MongoCollectionTest";
	private String collectionName = "collection";
	private int _1MB = 1024*1024;
	
	@Before
	public void beforeMongoCollectionTest(){
		mongo.dropDatabase(databaseName);
	}
	
	@Test
	public void testCapped(){
		
		DB db = mongo.getDB(databaseName);
		DBCollection c = db.createCollection(collectionName, getCappedOption());
		c.insert(createDbObject());
		
		if(logger.isInfoEnabled()){
			logger.info("collection exists:" + db.collectionExists(collectionName));
		}
		
		
		
		
	}

	private DBObject createDbObject() {
		
		DBObject object = new BasicDBObject();
		object.put("name", "name");
		return object;
	}

	private DBObject getCappedOption() {
	      DBObject options = new BasicDBObject();
	      options.put("capped", true);
	      options.put("size", 100 * _1MB);//max db file size in bytes
	      return options;
	}

}
