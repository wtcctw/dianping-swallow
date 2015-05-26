package com.dianping.swallow.common.server.monitor.data.structure;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.TimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:43:38
 */
public class MessageInfo extends AbstractTotalable implements Mergeable, Serializable{

	private static final long serialVersionUID = 1L;

	private AtomicLong totalDelay = new AtomicLong();

	private AtomicLong total = new AtomicLong();
	
	public MessageInfo(){
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MessageInfo)){
			return false;
		}
		MessageInfo cmp = (MessageInfo) obj;
		
		return cmp.totalDelay.get() == totalDelay.get() 
				&& cmp.total.get() == total.get();
	}
	
	@Override
	public int hashCode() {
		return (int) (totalDelay.get() ^ total.get());
	}

	public void addMessage(long messageId, long startTime, long endTime) {
		total.incrementAndGet();
		if(endTime < startTime){
			throw new TimeException("start > end", startTime, endTime);
		}
		totalDelay.addAndGet(endTime - startTime);
	}

	public long getTotalDelay() {
		return totalDelay.get();
	}

	public long getTotal() {
		return total.get();
	}
	
	@Override
	public void merge(Mergeable merge) {
		
		if(!(merge instanceof MessageInfo)){
			throw new IllegalArgumentException("wrong type " + merge.getClass());
		}
		MessageInfo toMerge = (MessageInfo) merge;
		total.addAndGet(toMerge.total.get());
		totalDelay.addAndGet(toMerge.totalDelay.get());
	}
	
	@Override
	public String toString() {
		return JsonBinder.getNonEmptyBinder().toJson(this);
	}

	@JsonIgnore
	public boolean isEmpty() {
		if(total.get() > 0 || totalDelay.get() > 0){
			return false;
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		
		MessageInfo info = (MessageInfo) super.clone();
		info.total = new AtomicLong(total.get());
		info.totalDelay = new AtomicLong(totalDelay.get());
		return info;
	}

}
