package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;



/**
 * @author mingdongli
 *
 * 2015年8月10日下午2:27:48
 */
@Document(collection = "PRODUCER_SERVER_RESOURCE")
public class ProducerServerResource extends ServerResource{
	
	private QPSAlarmSetting saveAlarmSetting;
	
	public QPSAlarmSetting getSaveAlarmSetting() {
		return saveAlarmSetting;
	}

	public void setSaveAlarmSetting(QPSAlarmSetting saveAlarmSetting) {
		this.saveAlarmSetting = saveAlarmSetting;
	}

	@Override
	public String toString() {
		return "ProducerServerResource [saveAlarmSetting=" + saveAlarmSetting + ", toString()=" + super.toString()
				+ "]";
	}

}
