package com.dianping.swallow.test.load.mongo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;



/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertCleanTest extends AbstractMongoTest {

	private static int messageCount = Integer.MAX_VALUE;
	private static int concurrentCount = 100;
	private static int topicCount = 2;
	
	private long[] lastMessageIds;
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length >= 1) {
			topicCount  = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			concurrentCount = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			messageCount = Integer.parseInt(args[2]);
		}

		new MongoInsertCleanTest().start();
	}

	@Override
	protected void doStart() throws InterruptedException, IOException {

		lastMessageIds = new long[topicCount];
		
		sendMessage(topicCount, concurrentCount, messageCount);
		scheduled.scheduleAtFixedRate(new CleanDataTask(topicName), 5, 5, TimeUnit.SECONDS);
	}

	class CleanDataTask implements Runnable{
		
		private String topicName;

		public CleanDataTask(String topicName){
			this.topicName = topicName;
		}
		
		@Override
		public void run() {
			try{
				if(logger.isInfoEnabled()){
					logger.info("[run][begin]");
				}
				
				int i = 0;
				for(long id : lastMessageIds){
					
					String topic = getTopicName(topicName, i);
					long start = System.currentTimeMillis();
//					int count = dao.deleteMessage(topic, id);
					if(logger.isInfoEnabled()){
						logger.info("[run][message delete]" + count + "," + (System.currentTimeMillis() - start)/1000 + "/s");
					}
					id = dao.getMaxMessageId(topic);
					if(logger.isInfoEnabled()){
						logger.info("[run]" + topic + "," +id);
					}
					lastMessageIds[i] = id;
					
					i++;
				}
			}catch(Throwable th){
				logger.error("", th);
			}
		}
	}
}
