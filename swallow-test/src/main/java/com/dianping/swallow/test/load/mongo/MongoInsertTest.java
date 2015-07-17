package com.dianping.swallow.test.load.mongo;

import java.io.IOException;

/**
 * @author mengwenchao
 * 
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertTest extends AbstractMongoTest {

	private static int concurrentCount = 100;
	private static int topicCount = 2;
	
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
			totalMessageCount = Integer.parseInt(args[2]);
		}
		if (args.length >= 4) {
			messageSize = Integer.parseInt(args[3]);
		}

		
		new MongoInsertTest().start();
	}

	@Override
	protected void doStart() throws InterruptedException, IOException {

		sendMessage(topicCount, concurrentCount, totalMessageCount);
	}

}
