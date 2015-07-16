package com.dianping.swallow.web.alarm;

/**
*
* @author qiyin
*
*/
public interface AlarmFilter {
	
	public boolean accept(AlarmFilterChain alarmFilterChain);

}
