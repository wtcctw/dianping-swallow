package com.dianping.swallow.web.alarmer;

/**
*
* @author qiyin
*
*/
public interface AlarmFilterChain {
	public boolean doNext();
	
	public void registerFilter(AlarmFilter alarmFilter);
	
	public void reset();
}
