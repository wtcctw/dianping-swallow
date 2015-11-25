package com.dianping.swallow.test.load;

/**
 * @author mengwenchao
 *
 * 2015年11月24日 下午3:34:53
 */
public abstract class AbstractLoadTask implements Runnable{
	
	protected String topicName;
	
	protected int concurrentIndex;
	
	public AbstractLoadTask(String topicName, int concurrentIndex) {
		
		this.topicName = topicName;
		this.concurrentIndex = concurrentIndex;
				
	}

	@Override
	public void run() {
		
		doRun();
		
	}

	protected abstract void doRun();

}
