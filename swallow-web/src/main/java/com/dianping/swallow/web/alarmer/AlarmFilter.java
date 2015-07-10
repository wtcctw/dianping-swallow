package com.dianping.swallow.web.alarmer;

/**
*
* @author qiyin
*
*/
public interface AlarmFilter {
	
	public boolean accept(AlarmFilterChain alarmFilterChain);

}
