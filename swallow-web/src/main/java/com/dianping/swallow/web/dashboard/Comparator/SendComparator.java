package com.dianping.swallow.web.dashboard.Comparator;

import java.util.Comparator;

import com.dianping.swallow.web.dashboard.model.Entry;


/**
 * @author mingdongli
 *
 * 2015年8月13日下午12:11:14
 */
public class SendComparator implements Comparator<Entry>{

	@Override
	public int compare(Entry e1, Entry e2) {
		
		int senddelay1 = e1.getSenddelayAlarm();
		int senddelay2 = e2.getSenddelayAlarm();
		
		if(senddelay1 == senddelay2){
			Float _f = e1.getNormalizedSendDelay();
			Float f = e2.getNormalizedSendDelay();
			
			return  _f.compareTo(f);
		}else{
			return senddelay1 - senddelay2;
		}
	}

}
