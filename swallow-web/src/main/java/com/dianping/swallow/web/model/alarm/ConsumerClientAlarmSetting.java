package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientAlarmSetting {

	private String consumerIdWhiteList;

	private ConsumerBaseAlarmSetting consumerIdDefaultSetting;

	private List<ConsumerIdAlarmSetting> consumerIdSettings;

	@Override
	public String toString() {
		return "ConsumerClientAlarmSetting [consumerIdWhiteList = " + consumerIdWhiteList
				+ ", consumerIdDefaultSetting = " + consumerIdDefaultSetting + ", consumerIdSettings = "
				+ consumerIdSettings + "]";
	}

}
