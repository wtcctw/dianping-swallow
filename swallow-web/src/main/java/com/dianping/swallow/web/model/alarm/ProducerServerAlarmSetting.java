package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerServerAlarmSetting extends AbstractServerAlarmSetting {

	private ProducerClientBaseAlarmSetting topicDefaultSetting;

	public ProducerClientBaseAlarmSetting getTopicDefaultSetting() {
		return topicDefaultSetting;
	}

	public void setTopicDefaultSetting(ProducerClientBaseAlarmSetting topicDefaultSetting) {
		this.topicDefaultSetting = topicDefaultSetting;
	}

	@Override
	public String toString() {
		return "ProducerServerAlarmSetting [topicDefaultSetting = " + topicDefaultSetting + " ]";
	}

}
