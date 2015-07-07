package com.dianping.swallow.web.alarmer;

/**
*
* @author qiyin
*
*/
public interface ConsumerStatisAlarmer extends Alarmer {
	
	public void doServerQPSAlarm();
	
	public void doTopicQPSAlarm();
	
	public void doConsumerIdQPSAlarm();
	
	public void doServerDelayAlarm();
	
	public void doTopicDelayAlarm();
	
	public void doConsumerIdDelayAlarm();
}
