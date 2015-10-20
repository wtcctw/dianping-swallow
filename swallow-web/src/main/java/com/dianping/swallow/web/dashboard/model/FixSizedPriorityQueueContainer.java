package com.dianping.swallow.web.dashboard.model;


import com.dianping.swallow.web.dashboard.comparator.AccuComparator;
import com.dianping.swallow.web.dashboard.comparator.AckComparator;
import com.dianping.swallow.web.dashboard.comparator.ConprehensiveComparator;
import com.dianping.swallow.web.dashboard.comparator.SendComparator;


/**
 * @author mingdongli
 *
 * 2015年8月14日上午11:05:26
 */
public class FixSizedPriorityQueueContainer {
	
	private static final int ENTRY_SIZE = 12;
	
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

	public FixSizedPriorityQueue getSendPriorityQueue() {
		return sendPriorityQueue;
	}

	public FixSizedPriorityQueue getAckPriorityQueue() {
		return ackPriorityQueue;
	}

	public FixSizedPriorityQueue getAccuPriorityQueue() {
		return accuPriorityQueue;
	}

}
