package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerServerAlarmSetting extends AbstractServerAlarmSetting{

	private ConsumerClientBaseAlarmSetting topicDefaultSetting;

	public ConsumerClientBaseAlarmSetting getTopicDefaultSetting() {
		return topicDefaultSetting;
	}

	public void setTopicDefaultSetting(ConsumerClientBaseAlarmSetting topicDefaultSetting) {
		this.topicDefaultSetting = topicDefaultSetting;
	}
	
	@Override
	public String toString() {
		return "ConsumerServerAlarmSetting [topicDefaultSetting = " + topicDefaultSetting + " ]";
	}
	
}
