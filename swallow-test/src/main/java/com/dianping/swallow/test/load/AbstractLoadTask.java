package com.dianping.swallow.test.load;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author mengwenchao
 *
 * 2015年11月24日 下午3:34:53
 */
public abstract class AbstractLoadTask implements Runnable{
	
	protected Logger logger = LogManager.getLogger(getClass());

	protected String topicName;
	
	protected int concurrentIndex;
	
	public AbstractLoadTask(String topicName, int concurrentIndex) {
		
		this.topicName = topicName;
		this.concurrentIndex = concurrentIndex;
				
	}

	@Override
	public void run() {

		try{
			doRun();
		}catch (CountExceedException e){
			logger.info("[run]" + e.getMessage());
		}
		
	}

	protected abstract void doRun();

}
