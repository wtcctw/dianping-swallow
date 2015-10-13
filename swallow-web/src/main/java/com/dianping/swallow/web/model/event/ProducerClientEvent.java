package com.dianping.swallow.web.model.event;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;

public class ProducerClientEvent extends ClientEvent {

	@Override
	public String getMessage(String template) {
		String message = template;
		if (StringUtils.isNotBlank(message)) {
			message = StringUtils.replace(message, AlarmMeta.TOPIC_TEMPLATE, getTopicName());
			message = StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, getIp());
			message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
		}
		return message;
	}

	@Override
	public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
		String key = getTopicName() + KEY_SPLIT + getIp() + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(lastAlarms, key, alarmMeta);
	}

	@Override
	public String toString() {
		return "ProducerClientEvent [" + super.toString() + "]";
	}

}
