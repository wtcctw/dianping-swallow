package com.dianping.swallow.web.alarmer;

/**
 *
 * @author qiyin
 *
 */
public interface ProducerStatisAlarmer extends Alarmer {
	public void doServerAlarm();

	public void doTopicAlarm();
}
