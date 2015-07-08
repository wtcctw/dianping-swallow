package com.dianping.swallow.web.model.alarm.backup;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientAlarmSetting {

	private List<String> consumerIdWhiteList;

	private ConsumerBaseAlarmSetting consumerIdDefaultSetting;

	private List<ConsumerIdAlarmSetting> consumerIdSettings;

	public List<String> getConsumerIdWhiteList() {
		return consumerIdWhiteList;
	}

	public void setConsumerIdWhiteList(List<String> consumerIdWhiteList) {
		this.consumerIdWhiteList = consumerIdWhiteList;
	}

	public ConsumerBaseAlarmSetting getConsumerIdDefaultSetting() {
		return consumerIdDefaultSetting;
	}

	public void setConsumerIdDefaultSetting(ConsumerBaseAlarmSetting consumerIdDefaultSetting) {
		this.consumerIdDefaultSetting = consumerIdDefaultSetting;
	}

	public List<ConsumerIdAlarmSetting> getConsumerIdSettings() {
		return consumerIdSettings;
	}

	public void setConsumerIdSettings(List<ConsumerIdAlarmSetting> consumerIdSettings) {
		this.consumerIdSettings = consumerIdSettings;
	}

	@Override
	public String toString() {
		return "ConsumerClientAlarmSetting [consumerIdWhiteList = " + consumerIdWhiteList
				+ ", consumerIdDefaultSetting = " + consumerIdDefaultSetting + ", consumerIdSettings = "
				+ consumerIdSettings + "]";
	}

}
