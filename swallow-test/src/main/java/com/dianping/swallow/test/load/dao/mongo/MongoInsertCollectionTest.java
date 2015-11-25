package com.dianping.swallow.test.load.dao.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.test.load.dao.AbstractDaoTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertCollectionTest extends AbstractDaoTest {

	private static int concurrentCount = 100;
	
	private static int dbCount = 1;
	private static int collectionCount = 1;
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		totalMessageCount = 1000;
		
		if (args.length >= 1) {
			dbCount  = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			collectionCount = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			concurrentCount = Integer.parseInt(args[2]);
		}
		if (args.length >= 4) {
			totalMessageCount = Integer.parseInt(args[3]);
		}
		if (args.length >= 5) {
			messageSize = Integer.parseInt(args[4]);
		}

		
		new MongoInsertCollectionTest().start();
	}

	@Override
	protected void doStart() throws InterruptedException, IOException {

		sendMessage();
	}

	private void sendMessage() throws IOException {

		MongoClient mongo = getMongo();
		
		for(int i=0;i<dbCount;i++){
			
			DB db = mongo.getDB("msg#" + getTopicName(topicName, i));
			
			for(int j=0;j<collectionCount;j++){
				
				DBCollection collection = db.getCollection("c" + j);
				
				for(int k=0;k<concurrentCount;k++){

					executors.execute(new TaskSaveMessage(collection));
				}
			}
		}
		
	}


	class TaskSaveMessage implements Runnable{
		
		private DBCollection collection;
		
		public TaskSaveMessage(DBCollection collection){
			this.collection = collection;
		}

		@Override
		public void run() {
			
			while(true){
				if(count.get() > totalMessageCount){
					if(logger.isInfoEnabled()){
						logger.info("[run] total message count " + totalMessageCount);
					}
					exit();
				}
				collection.save(createSimpleDataObject());
				count.incrementAndGet();
			}
		}
	}
	
	private DBObject createSimpleDataObject() {
		
		DBObject object = new BasicDBObject();
		object.put("c", message);
		object.put("_id", new BSONTimestamp());
		object.put("t", System.currentTimeMillis());
		object.put("t1", System.currentTimeMillis());
		return object;
	}


	/**
	 * @return
	 * @throws IOException 
	 */
	private String getTopicToMongo() throws IOException {
		
		Properties p = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("swallow-store-lion.properties");
		if(ins == null){
			throw new IllegalStateException("file not found: swallow-store-lion.properties");
		}
		p.load(ins);
		String result = p.getProperty("swallow.topiccfg.default");
		if(StringUtils.isBlank(result)){
			throw new IllegalStateException("swallow.mongo.producerServerURI not found!!!");
		}
		return result;
	}

	
	protected MongoClient getMongo() throws IOException{
		
		String topicToMongo = getTopicToMongo();
		ServerAddress address = getAddress(topicToMongo);
		return new MongoClient(address);
	}

	/**
	 * 获取默认地址
	 * @param topicToMongo
	 * @return
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	private ServerAddress getAddress(String topicToMongo) throws NumberFormatException, UnknownHostException {

		TopicConfig config = JsonBinder.getNonEmptyBinder().fromJson(topicToMongo, TopicConfig.class);
		
		String address = config.getStoreUrl().substring("mongodb://".length());
		String []ipPort = address.split(":");
		return new ServerAddress(ipPort[0], Integer.parseInt(ipPort[1]));
				
	}

}
