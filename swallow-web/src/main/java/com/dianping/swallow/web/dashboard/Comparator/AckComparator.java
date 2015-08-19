package com.dianping.swallow.web.dashboard.Comparator;

import java.util.Comparator;

import com.dianping.swallow.web.dashboard.model.Entry;


/**
 * @author mingdongli
 *
 * 2015年8月13日下午12:11:40
 */
public class AckComparator implements Comparator<Entry>{

	@Override
	public int compare(Entry e1, Entry e2) {
		
		int ackdelay1 = e1.getAckdelayAlarm();
		int ackdelay2 = e2.getAckdelayAlarm();
		
		if(ackdelay1 == ackdelay2){
			
			Float _f = e1.getNormalizedAckDelay();
			Float f = e2.getNormalizedAckDelay();
			
			return  _f.compareTo(f);
		}else{
			return ackdelay1 - ackdelay2;
		}
		
	}
	
}
