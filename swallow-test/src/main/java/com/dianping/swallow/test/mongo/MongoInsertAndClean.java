package com.dianping.swallow.test.mongo;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClient;

/**
 * @author mengwenchao
 *
 * 2015年3月23日 下午2:05:10
 */
public class MongoInsertAndClean{
	
	private ExecutorService executors = Executors.newCachedThreadPool(); 
	private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);
	
	private MongoClient mongoClient;

	public static void main(String[] args) throws UnknownHostException {

		new MongoInsertAndClean().start();
	}

	private void start() throws UnknownHostException {
		
		mongoClient = new MongoClient("192.168.214.143", 27017);
		
		executors.execute(new InsertTask());
		scheduled.scheduleAtFixedRate(new CleanTask(), 0, 60, TimeUnit.SECONDS);
	}

	class InsertTask implements Runnable{
		
		@Override
		public void run() {
			
			insertData();
		}
		
		private void insertData() {
		}
		
	}


	class CleanTask implements Runnable{
		@Override
		public void run() {
			
		}
	}
}
