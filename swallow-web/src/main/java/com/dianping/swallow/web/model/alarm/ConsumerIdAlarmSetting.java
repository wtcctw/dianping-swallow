package com.dianping.swallow.web.model.alarm;

import java.util.List;


public class ConsumerIdAlarmSetting extends BaseAlarmSetting {
	
	private List<String> whiteList;
	
	private ConsumerBaseAlarmSetting consumerAlarmSetting;
	
	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}

	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}
}
