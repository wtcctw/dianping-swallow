package com.dianping.swallow.test.load.dao.mongo;

import java.util.concurrent.TimeUnit;

import com.dianping.swallow.test.load.dao.AbstractDaoTest;



/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertCleanTest extends AbstractDaoTest {

	private long[] lastMessageIds;
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		new MongoInsertCleanTest().start();
	}

	@Override
	protected void doStart() throws Exception {

		super.doStart();
		
		lastMessageIds = new long[topicCount];
		
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
						logger.info("[run][message delete]" + getCurrentCount() + "," + (System.currentTimeMillis() - start)/1000 + "/s");
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
