package com.dianping.swallow.web.alarm;

/**
*
* @author qiyin
*
*/
public interface AlarmFilterChain {
	
	public void setChainName(String chainName);
	
	public String getChainName();
	
	public boolean doNext();
	
	public void registerFilter(AlarmFilter alarmFilter);
	
	public void reset();
}
