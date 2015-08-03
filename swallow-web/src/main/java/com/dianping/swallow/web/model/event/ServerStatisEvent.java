package com.dianping.swallow.web.model.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;

public class ServerStatisEvent extends StatisEvent {

	private static final Map<String, Long> lastAlarms = new ConcurrentHashMap<String, Long>();

	private String ip;

	public String getIp() {
		return ip;
	}

	public ServerStatisEvent setIp(String ip) {
		this.ip = ip;
		return this;
	}

	@Override
	public String toString() {
		return "ServerStatisEvent [ip=" + ip + "]";
	}

	@Override
	public void alarm() {
		switch (getEventType()) {
		case CONSUMER:
			switch (getStatisType()) {
			case SENDQPS_PEAK:
				sendMessage(AlarmType.CONSUMER_SERVER_SENDQPS_PEAK);
				break;
			case SENDQPS_VALLEY:
				sendMessage(AlarmType.CONSUMER_SERVER_SENDQPS_VALLEY);
				break;
			case SENDQPS_FLU:
				sendMessage(AlarmType.CONSUMER_SERVER_SENDQPS_FLUCTUATION);
				break;
			case ACKQPS_PEAK:
				sendMessage(AlarmType.CONSUMER_SERVER_ACKQPS_PEAK);
				break;
			case ACKQPS_VALLEY:
				sendMessage(AlarmType.CONSUMER_SERVER_ACKQPS_VALLEY);
				break;
			case ACKQPS_FLU:
				sendMessage(AlarmType.CONSUMER_SERVER_ACKQPS_FLUCTUATION);
				break;
			default:
				break;
			}
		case PRODUCER:
			switch (getStatisType()) {
			case SENDQPS_PEAK:
				sendMessage(AlarmType.PRODUCER_SERVER_QPS_PEAK);
				break;
			case SENDQPS_VALLEY:
				sendMessage(AlarmType.PRODUCER_SERVER_QPS_VALLEY);
				break;
			case SENDQPS_FLU:
				sendMessage(AlarmType.PRODUCER_SERVER_QPS_FLUCTUATION);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String createMessage(String template) {
		String message = template;
		if (StringUtils.isNotBlank(message)) {
			message = StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, getIp());
			message = StringUtils.replace(message, AlarmMeta.CURRENTVALUE_TEMPLATE, Long.toString(getCurrentValue()));
			message = StringUtils.replace(message, AlarmMeta.EXPECTEDVALUE_TEMPLATE, Long.toString(getExpectedValue()));
			message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
		}
		return message;
	}

	@Override
	public String createRelatedInfo() {
		return ip;
	}

	@Override
	public boolean isSendAlarm(AlarmType alarmType, int timeSpan) {
		String key = ip + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(lastAlarms, key, timeSpan);
	}

	@Override
	public Set<String> getRelatedIps() {
		Set<String> ips = new HashSet<String>();
		ips.add(ip);
		return ips;
	}

}
