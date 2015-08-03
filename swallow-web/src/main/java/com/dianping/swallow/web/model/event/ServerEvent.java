package com.dianping.swallow.web.model.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 上午11:13:23
 */
@Service
@Scope("prototype")
public class ServerEvent extends Event {

	private static final Map<String, Long> lastAlarms = new ConcurrentHashMap<String, Long>();

	private String ip;

	private String slaveIp;

	private ServerType serverType;

	public String getIp() {
		return ip;
	}

	public ServerEvent setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getSlaveIp() {
		return slaveIp;
	}

	public ServerEvent setSlaveIp(String slaveIp) {
		this.slaveIp = slaveIp;
		return this;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public ServerEvent setServerType(ServerType serverType) {
		this.serverType = serverType;
		return this;
	}

	@Override
	public String toString() {
		return "ServerEvent [ip=" + ip + ", slaveIp=" + slaveIp + ", serverType=" + serverType + "]";
	}

	@Override
	public void alarm() {
		switch (getEventType()) {
		case CONSUMER:
			switch (getServerType()) {
			case SERVER_SENDER:
				sendMessage(AlarmType.CONSUMER_SERVER_SENDER);
				break;
			case SERVER_SENDER_OK:
				sendMessage(AlarmType.CONSUMER_SERVER_SENDER_OK);
				break;
			case SLAVEPORT_OPENED:
				sendMessage(AlarmType.CONSUMER_SERVER_SLAVEPORT_OPENED);
				break;
			case BOTHPORT_OPENED:
				sendMessage(AlarmType.CONSUMER_SERVER_BOTHPORT_OPENED);
				break;
			case BOTHPORT_UNOPENED:
				sendMessage(AlarmType.CONSUMER_SERVER_BOTHPORT_UNOPENED);
				break;
			case PORT_OPENED_OK:
				sendMessage(AlarmType.CONSUMER_SERVER_PORT_OPENED_OK);
				break;
			default:
				break;
			}
		case PRODUCER:
			switch (getServerType()) {
			case SERVER_SENDER:
				sendMessage(AlarmType.PRODUCER_SERVER_SENDER);
				break;
			case SERVER_SENDER_OK:
				sendMessage(AlarmType.PRODUCER_SERVER_SENDER_OK);
				break;
			case PIGEON_SERVICE:
				sendMessage(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE);
				break;
			case PIGEON_SERVICE_OK:
				sendMessage(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE_OK);
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
			message = StringUtils.replace(message, AlarmMeta.MASTERIP_TEMPLATE, getIp());
			message = StringUtils.replace(message, AlarmMeta.SLAVEIP_TEMPLATE, getSlaveIp());
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
