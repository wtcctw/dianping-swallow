package com.dianping.swallow.web.dashboard.model;


import java.util.Date;

import com.dianping.swallow.web.dashboard.Comparator.AccuComparator;
import com.dianping.swallow.web.dashboard.Comparator.AckComparator;
import com.dianping.swallow.web.dashboard.Comparator.ConprehensiveComparator;
import com.dianping.swallow.web.dashboard.Comparator.SendComparator;


/**
 * @author mingdongli
 *
 * 2015年8月14日上午11:05:26
 */
public class FixSizedPriorityQueueContainer {
	
	private static final int ENTRY_SIZE = 12;
	
	private Date time; 

	private FixSizedPriorityQueue comprehensivePriorityQueue =  new FixSizedPriorityQueue(ENTRY_SIZE , new ConprehensiveComparator() );

	private FixSizedPriorityQueue sendPriorityQueue =  new FixSizedPriorityQueue(ENTRY_SIZE , new SendComparator() );
	
	private FixSizedPriorityQueue ackPriorityQueue =  new FixSizedPriorityQueue(ENTRY_SIZE , new AckComparator() );
	
	private FixSizedPriorityQueue accuPriorityQueue =  new FixSizedPriorityQueue(ENTRY_SIZE , new AccuComparator() );
	
	public boolean addEntry(Entry entry){
		
		try{
			comprehensivePriorityQueue.add(entry);
			sendPriorityQueue.add(entry);
			ackPriorityQueue.add(entry);
			accuPriorityQueue.add(entry);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}

	public FixSizedPriorityQueue getComprehensivePriorityQueue() {
		return comprehensivePriorityQueue;
	}

	public void setComprehensivePriorityQueue(FixSizedPriorityQueue comprehensivePriorityQueue) {
		this.comprehensivePriorityQueue = comprehensivePriorityQueue;
	}

	public FixSizedPriorityQueue getSendPriorityQueue() {
		return sendPriorityQueue;
	}

	public void setSendPriorityQueue(FixSizedPriorityQueue sendPriorityQueue) {
		this.sendPriorityQueue = sendPriorityQueue;
	}

	public FixSizedPriorityQueue getAckPriorityQueue() {
		return ackPriorityQueue;
	}

	public void setAckPriorityQueue(FixSizedPriorityQueue ackPriorityQueue) {
		this.ackPriorityQueue = ackPriorityQueue;
	}

	public FixSizedPriorityQueue getAccuPriorityQueue() {
		return accuPriorityQueue;
	}

	public void setAccuPriorityQueue(FixSizedPriorityQueue accuPriorityQueue) {
		this.accuPriorityQueue = accuPriorityQueue;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
}
