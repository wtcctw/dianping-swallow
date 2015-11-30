package com.dianping.swallow.test.load.dao.mongo;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoMessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.test.load.dao.AbstractDaoTest;

/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoBatchInsertTest extends AbstractDaoTest {

	private static int batchSize = 10;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int argIndex = 0;
		if (args.length >= 1) {
			batchSize = Integer.parseInt(args[argIndex++]);
		}
		
		new MongoBatchInsertTest().start();
	}


	protected void saveMessge(String topicName) {

		MongoMessageDAO impl = (MongoMessageDAO) dao;
		
		List<SwallowMessage> messages = createMessages();
		impl.saveMessage(topicName, messages);

		addAndGetCurrentCount(batchSize);
	}

	private List<SwallowMessage> createMessages() {
		
		List<SwallowMessage> messages = new LinkedList<SwallowMessage>();
		
		for(int i=0 ; i < batchSize ; i++){
			messages.add(createMessage(message));
		}
		
		return messages;
	}

}
