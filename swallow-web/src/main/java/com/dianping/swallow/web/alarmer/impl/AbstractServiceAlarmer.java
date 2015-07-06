package com.dianping.swallow.web.alarmer.impl;

/**
*
* @author qiyin
*
*/
public abstract class AbstractServiceAlarmer extends AbstractAlarmer {
	
	@Override
	public void doAlarm(){
		doCheckProcess();
		doCheckPort();
		doCheckService();
	}
	
	public abstract void doCheckProcess();
	
	public abstract void doCheckPort();
	
	public abstract void doCheckService();

}
