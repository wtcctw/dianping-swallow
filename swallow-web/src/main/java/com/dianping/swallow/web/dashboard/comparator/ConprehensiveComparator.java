package com.dianping.swallow.web.dashboard.comparator;

import java.util.Comparator;

import com.dianping.swallow.web.dashboard.model.Entry;


/**
 * @author mingdongli
 *
 * 2015年8月13日下午12:13:36
 */
public class ConprehensiveComparator implements Comparator<Entry>{

	@Override
	public int compare(Entry e1, Entry e2) {  
		int numAlarm = e1.getNumAlarm().compareTo(e2.getNumAlarm());
		if (numAlarm == 0) {
			Float _f = e1.getNormalizedSendDelay() + e1.getNormalizedAckDelay() + e1.getNormalizedAccu();
			Float f = e2.getNormalizedSendDelay() + e2.getNormalizedAckDelay() + e2.getNormalizedAccu();

			return _f.compareTo(f);
		} else {
			return numAlarm;
		}
    }  

}
