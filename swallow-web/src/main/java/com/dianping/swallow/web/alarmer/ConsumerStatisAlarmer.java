package com.dianping.swallow.web.alarmer;

/**
*
* @author qiyin
*
*/
public interface ConsumerStatisAlarmer extends Alarmer {
	
	public void doServerQpsAlarm();
	
	public void doTopicQpsAlarm();
	
	public void doConsumerIdQpsAlarm();
	
	public void doServerDelayAlarm();
	
	public void doTopicDelayAlarm();
	
	public void doConsumerIdDelayAlarm();
}
