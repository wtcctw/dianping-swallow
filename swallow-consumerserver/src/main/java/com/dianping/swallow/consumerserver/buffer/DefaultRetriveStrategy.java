package com.dianping.swallow.consumerserver.buffer;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 默认从mongodb取数据策略
 * 
 * @author mengwenchao
 *
 * 2014年11月6日 下午2:17:21
 */
public class DefaultRetriveStrategy implements RetriveStrategy{
	
	
	private Deque<RetrieveStatus> status = new LinkedList<DefaultRetriveStrategy.RetrieveStatus>();

	private final int statusCount = 10;
	
	private int zeroRetrieveInterval = 20;//如果上次读取的数据为0，则在此时间内不读取数据
	
	
	public DefaultRetriveStrategy(){
		
	}

	public DefaultRetriveStrategy(int zeroRetrieveInterval){
		this.zeroRetrieveInterval = zeroRetrieveInterval;
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
}
