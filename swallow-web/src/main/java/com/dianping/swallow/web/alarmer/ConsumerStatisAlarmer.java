package com.dianping.swallow.web.alarmer;

/**
*
* @author qiyin
*
*/
public interface ConsumerStatisAlarmer extends Alarmer {
	
	public void doServerAlarm();
	
	public void doConsumerIdAlarm();
	
	public void doTopicAlarm();
	
}
