package com.dianping.swallow.consumerserver.buffer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;


/**
 * 默认从mongodb取数据策略
 * 
 * @author mengwenchao
 *
 * 2014年11月6日 下午2:17:21
 */
public class DefaultRetrieveStrategy implements RetrieveStrategy {
	
	private  final Logger          logger  = LogManager.getLogger(getClass());

	private Deque<RetrieveStatus> status = new LinkedList<DefaultRetrieveStrategy.RetrieveStatus>();
	
	private ConsumerInfo consumerInfo;

	private final int statusCount = 10;
	
	/*上次读取消息数值小于此值，间隔一定时间再取*/
	private int minRetrieveCount = 10;
	
	private int minRetrieveInterval = 20;//如果上次读取的数据小于minRetrieveCount，则在此时间内不读取数据
	private long maxRetrieveInterval = 500;
	/**
	 * 如果读取消息一直为0，不停延时
	 */
	private int zeroCount = 0;
	
	/*队列最大消息数，大于此值，不取*/
	private int maxThreshold;
		
	private AtomicInteger messageCount = new AtomicInteger();
	
	public DefaultRetrieveStrategy(ConsumerInfo consumerInfo, int minRetrieveInterval, int maxThreshold, int maxRetriverTaskCountPerConsumer){
		
		this.minRetrieveInterval = minRetrieveInterval;
		this.maxThreshold = maxThreshold;
		this.consumerInfo = consumerInfo;
		this.maxRetriverTaskCountPerConsumer = maxRetriverTaskCountPerConsumer;
	}

	@Override
	public boolean isRetrieve() {
		
		RetrieveStatus rs = status.peekLast();
		if(rs == null){
			return true;
		}
		
		long currentTime = System.currentTimeMillis();
		
		if(zeroCount > 0 && (currentTime - rs.getRetrieveTime() < getZeroDelayTime())){
			return false;
		}
				
		if(rs.getCount() <= minRetrieveCount && (currentTime - rs.getRetrieveTime() < minRetrieveInterval)){
			return false;
		}
		
		if(messageCount.get() >= maxThreshold){
			if(logger.isInfoEnabled()){
				logger.info("[isRetrieve][message exceed maxthreshold]" + consumerInfo + "," + maxThreshold + ", " + messageCount.get());
			}
			return false;
		}
		
		return true;
	}

	private long getZeroDelayTime() {
		
		long delay = minRetrieveInterval*zeroCount;
		
		if(delay > maxRetrieveInterval || delay <= 0){
			delay = maxRetrieveInterval;
		}
		return delay;
	}

	@Override
	public synchronized void retrieved(int count) {
		
		if(count == 0){
			zeroCount++;
		}else{
			zeroCount = 0;
		}
		
		status.offer(new RetrieveStatus(count, System.currentTimeMillis()));
		if(status.size() > statusCount){
			status.poll();
		}
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

	@Override
	public void beginRetrieve() {
	}

	@Override
	public void endRetrieve() {
		taskCount.decrementAndGet();
	}

	
	private AtomicInteger taskCount =  new AtomicInteger(); 
	private int maxRetriverTaskCountPerConsumer = 3;
	
	@Override
	public boolean canPutNewTask() {
		
		if(taskCount.get() >= maxRetriverTaskCountPerConsumer){
			return false;
		}
		return true; 
	}

	@Override
	public void offerNewTask() {
		taskCount.incrementAndGet();
	}

	public int getMaxRetriverTaskCountPerConsumer() {
		return maxRetriverTaskCountPerConsumer;
	}

	@Override
	public int messageCount() {
		return messageCount.get();
	}

}
