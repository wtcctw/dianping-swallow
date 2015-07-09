package com.dianping.swallow.web.alarmer;

public interface AlarmFilterChain {
	public boolean doNext();
	
	public void registerFilter(AlarmFilter alarmFilter);
	
	public void reset();
}
