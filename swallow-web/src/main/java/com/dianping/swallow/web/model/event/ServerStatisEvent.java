package com.dianping.swallow.web.model.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.manager.AlarmReceiverManager.AlarmReceiver;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:13:29
 */
public class ServerStatisEvent extends StatisEvent {

	private static final Map<String, AlarmRecord> lastAlarms = new ConcurrentHashMap<String, AlarmRecord>();

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
			break;
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
	public String getMessage(String template) {
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
	public String getRelated() {
		return ip;
	}

	@Override
	public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
		String key = ip + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(lastAlarms, key, alarmMeta);
	}

	@Override
	public AlarmReceiver getRelatedReceiver() {
		return this.receiverManager.getAlarmReceiverByIp(ip);
	}

	@Override
	public RelatedType getRelatedType() {
		switch (getEventType()) {
		case PRODUCER:
			return RelatedType.P_SERVER_IP;
		case CONSUMER:
			return RelatedType.C_SERVER_IP;
		}
		return null;
	}

}
