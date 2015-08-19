package com.dianping.swallow.web.dashboard.Comparator;

import java.util.Comparator;

import com.dianping.swallow.web.dashboard.model.Entry;


/**
 * @author mingdongli
 *
 * 2015年8月13日下午12:09:39
 */
public class AccuComparator implements Comparator<Entry>{

	@Override
	public int compare(Entry e1, Entry e2) {
		
		int accu1 = e1.getAccuAlarm();
		int accu2 = e2.getAccuAlarm();
		
		if(accu1 == accu2){
			
			Float _f = e1.getNormalizedAccu();
			Float f = e2.getNormalizedAccu();
			
			return  _f.compareTo(f);
		}else{
			return accu1 - accu2;
		}
		
	}
}
