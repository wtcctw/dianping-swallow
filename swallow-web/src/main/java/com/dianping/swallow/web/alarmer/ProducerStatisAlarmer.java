package com.dianping.swallow.web.alarmer;

/**
 *
 * @author qiyin
 *
 */
public interface ProducerStatisAlarmer extends Alarmer {
	public void doServerQPSAlarm();

	public void doTopicQPSAlarm();

	public void doServerDelayAlarm();

	public void doTopicDelayAlarm();
}
