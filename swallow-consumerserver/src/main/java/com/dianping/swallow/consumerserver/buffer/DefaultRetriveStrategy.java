package com.dianping.swallow.consumerserver.buffer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

/**
 * 默认从mongodb取数据策略
 * 
 * @author mengwenchao
 *
 * 2014年11月6日 下午2:17:21
 */
public class DefaultRetriveStrategy implements RetriveStrategy{
	
	private  final Logger          logger  = LoggerFactory.getLogger(getClass());

	private Deque<RetrieveStatus> status = new LinkedList<DefaultRetriveStrategy.RetrieveStatus>();
	
	private ConsumerInfo consumerInfo;

	private final int statusCount = 10;
	
	private int zeroRetrieveInterval = 20;//如果上次读取的数据为0，则在此时间内不读取数据
	
	/*队列最大消息数，大于此值，不取*/
	private int maxThreshold;
	
	private AtomicInteger messageCount = new AtomicInteger();
	
	public DefaultRetriveStrategy(ConsumerInfo consumerInfo, int zeroRetrieveInterval, int maxThreshold){
		
		this.zeroRetrieveInterval = zeroRetrieveInterval;
		this.maxThreshold = maxThreshold;
		this.consumerInfo = consumerInfo;
	}

	@Override
	public boolean isRetrieve() {
		
		RetrieveStatus rs = status.peekLast();
		if(rs == null){
			return true;
		}
		
		long currentTime = System.currentTimeMillis();
		if(rs.getCount() == 0 && (currentTime - rs.getRetrieveTime() < zeroRetrieveInterval)){
			return false;
		}
		
		if(messageCount.get() >= maxThreshold){
			if(logger.isInfoEnabled()){
				logger.info("[isRetrieve][message exceed maxthreshold]" + consumerInfo + "," + maxThreshold);
			}
			return false;
		}
		
		return true;
	}

	@Override
	public synchronized void retrieved(int count) {
		
		status.offer(new RetrieveStatus(count, System.currentTimeMillis()));
		if(status.size() > statusCount){
			status.poll();
		}
	}

	public int getZeroRetrieveInterval() {
		return zeroRetrieveInterval;
	}

	public void setZeroRetrieveInterval(int zeroRetrieveInterval) {
		
		this.zeroRetrieveInterval = zeroRetrieveInterval;
	}

	static class RetrieveStatus{
		
		private int count;
		private long retrieveTime;
		
		public RetrieveStatus(int count, long retireveTime){
			this.count = count;
			this.retrieveTime = retireveTime;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public long getRetrieveTime() {
			return retrieveTime;
		}
		public void setRetrieveTime(long retrieveTime) {
			this.retrieveTime = retrieveTime;
		}
		
	}

	@Override
	public void increaseMessageCount() {
		messageCount.incrementAndGet();
	}

	@Override
	public void increaseMessageCount(int count) {
		messageCount.addAndGet(count);
	}

	@Override
	public void decreaseMessageCount() {
		messageCount.decrementAndGet();
	}

	@Override
	public void decreaseMessageCount(int count) {
		messageCount.addAndGet(-count);
	}
}
