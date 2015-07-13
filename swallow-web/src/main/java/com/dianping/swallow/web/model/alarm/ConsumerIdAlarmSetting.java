package com.dianping.swallow.web.model.alarm;


public class ConsumerIdAlarmSetting extends BaseAlarmSetting {
	
	private ConsumerBaseAlarmSetting consumerAlarmSetting;
	
	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}
}
