package com.dianping.swallow.test.load.mongo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoBatchInsertTest extends AbstractMongoTest {

	private static int concurrentCount = 100;
	private static int topicCount = 2;
	private static int batchSize = 10;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int argIndex = 0;
		if (args.length >= 1) {
			topicCount  = Integer.parseInt(args[argIndex++]);
		}
		if (args.length >= 2) {
			concurrentCount = Integer.parseInt(args[argIndex++]);
		}
		if (args.length >= 3) {
			totalMessageCount = Integer.parseInt(args[argIndex++]);
		}
		if (args.length >= 4) {
			batchSize = Integer.parseInt(args[argIndex++]);
		}
		if (args.length >= 5) {
			messageSize = Integer.parseInt(args[argIndex++]);
		}

		
		new MongoBatchInsertTest().start();
	}

	@Override
	protected void doStart() throws InterruptedException, IOException {
		
		if(logger.isInfoEnabled()){
			logger.info("batchSize:" + batchSize + ",messageSize" + messageSize);
		}

		sendMessage(topicCount, concurrentCount, totalMessageCount);
	}

	protected void saveMessge(String topicName) {

		MessageDAOImpl impl = (MessageDAOImpl) dao;
		
		List<SwallowMessage> messages = createMessages();
		impl.saveMessage(topicName, messages);
		
		count.addAndGet(batchSize);
	}

	private List<SwallowMessage> createMessages() {
		
		List<SwallowMessage> messages = new LinkedList<SwallowMessage>();
		
		for(int i=0 ; i < batchSize ; i++){
			messages.add(createMessage(message));
		}
		
		return messages;
	}

}
