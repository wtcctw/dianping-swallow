package com.dianping.swallow.test.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年11月24日 下午3:34:53
 */
public abstract class AbstractLoadTask implements Runnable{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

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
