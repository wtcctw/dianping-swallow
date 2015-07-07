package com.dianping.swallow.web.alarmer;

/**
 *
 * @author qiyin
 *
 */
public interface ProducerStatisAlarmer extends Alarmer {
	public void doServerQpsAlarm();

	public void doTopicQpsAlarm();

	public void doServerDelayAlarm();

	public void doTopicDelayAlarm();
}
