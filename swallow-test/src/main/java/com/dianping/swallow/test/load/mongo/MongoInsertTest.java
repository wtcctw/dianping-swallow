package com.dianping.swallow.test.load.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.test.load.producer.AbstractProducerLoadTest;

/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertTest extends AbstractMongoTest {

	private static int messageCount = Integer.MAX_VALUE;
	private static int concurrentCount = 100;
	private static int topicCount = 2;
	private MessageDAO dao;
	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		
		if (args.length >= 1) {
			topicCount  = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			concurrentCount = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			messageCount = Integer.parseInt(args[2]);
		}

		new MongoInsertTest().start();
	}

	@Override
	protected void doStart() throws InterruptedException, IOException {

		logger.info("[doStart][config]" + topicCount + "," + concurrentCount + "," + messageCount);
		
		for(int i = 0 ;i< topicCount; i++){
			
			for(int j=0;j<concurrentCount;j++){
				
				final String realTopicName = getTopicName(topicName, i);
				executors.execute(new Runnable(){
					@Override
					public void run() {
	
						while(true){
							try{
								if(count.get() > messageCount){
									break;
								}
								dao.saveMessage(realTopicName, createMessage(message));
								count.incrementAndGet();
							}catch(Exception e){
								logger.error("error save message", e);
							}finally{
							}
						}
						
					}
				});
			}
		}

	}

}
