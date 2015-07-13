package com.dianping.swallow.web.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.manager.IPDescManager;

/**
 * 
 * @author qiyin
 *
 */
@Service("alarmManager")
public class AlarmManagerImpl implements AlarmManager {

	private static final String TOTAL_KEY = "total";

	private static final String COMMA_SPLIT = ",";

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private IPDescManager ipDescManager;

	@Override
	public void producerServiceAlarm(String ip) {
		String message = "producer server is not work, please immediately repair. [date] " + new Date().toString();
		sendAlarmByIp(ip, "ProducerServerService Alarm", message, AlarmLevelType.CRITICAL);
	}

	@Override
	public void producerSenderAlarm(String ip) {
		String message = "producer server [ip] " + ip
				+ " is not sender statistics data to web, please immediately repair. [date] " + new Date().toString();
		sendAlarmByIp(ip, "ProducerServerSender Alarm", message, AlarmLevelType.CRITICAL);
	}

	@Override
	public void producerServerStatisQpsPAlarm(String serverIp, long qpx) {
		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ProducerServerQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerServerStatisQpsVAlarm(String serverIp, long qpx) {
		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx
				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ProducerServerQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerServerStatisQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ProducerServerQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerTopicStatisQpsPAlarm(String topic, long qpx) {
		String message = "producer [topic] " + topic + " [qpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerTopicStatisQpsVAlarm(String topic, long qpx) {
		String message = "producer [topic] " + topic + " [qpx = ]" + qpx
				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerTopicStatisQpsFAlarm(String topic, long qpx, long expected) {
		String message = "producer [topic] " + topic + " [qpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void producerTopicStatisQpsDAlarm(String topic, long delay, long expected) {
		String message = "producer [topic] " + topic + " [delay = ]" + delay + " [expected = ]" + expected
				+ " is too long, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerPortAlarm(String masterIp, String slaveIp, boolean isBoth) {
		String message = "";
		if (isBoth) {
			message = "consumer server port [masterIp] " + masterIp + " [slaveIp] " + slaveIp
					+ "port both are opened, please immediately repair. [date] " + new Date().toString();
		} else {
			message = "consumer server port [masterIp] " + masterIp + " [slaveIp] " + slaveIp
					+ " slave port is opened, please immediately repair. [date] " + new Date().toString();
		}
		sendAlarmByIp(masterIp, "ConsumerServerPort Alarm", message, AlarmLevelType.CRITICAL);
	}

	@Override
	public void consumerSenderAlarm(String ip) {

		String message = "consumer server [ip] " + ip
				+ " is not sender statistics data to web, please immediately repair. [date] " + new Date().toString();
		sendAlarmByIp(ip, "ConsumerServerSender Alarm", message, AlarmLevelType.CRITICAL);
	}

	@Override
	public void consumerServerStatisSQpsPAlarm(String serverIp, long qpx) {
		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerServerStatisSQpsVAlarm(String serverIp, long qpx) {
		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx
				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerServerStatisSQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerServerStatisAQpsPAlarm(String serverIp, long qpx) {
		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerServerStatisAQpsVAlarm(String serverIp, long qpx) {
		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx
				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerServerStatisAQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByIp(serverIp, "ConsumerServerAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisSQpsPAlarm(String topic, long qpx) {
		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisSQpsVAlarm(String topic, long qpx) {
		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx
				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisSQpsFAlarm(String topic, long qpx, long expected) {
		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisSQpsDAlarm(String topic, long delay, long expected) {
		String message = "consumer [topic] " + topic + " [senddelay = ]" + delay + " [expected = ]" + expected
				+ " is too long, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisAQpsPAlarm(String topic, long qpx) {
		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisAQpsVAlarm(String topic, long qpx) {
		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx
				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisAQpsFAlarm(String topic, long qpx, long expected) {
		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx + " [expected = ]" + expected
				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerTopicStatisAQpsDAlarm(String topic, long delay, long expected) {
		String message = "consumer [topic] " + topic + " [ackdelay = ]" + delay + " [expected = ]" + expected
				+ " is too long, please immediately check. [date] " + new Date().toString();
		sendAlarmByProducerTopic(topic, "ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisSQpsPAlarm(String topic, String consumerId, long qpx) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisSQpsVAlarm(String topic, String consumerId, long qpx) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisSQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
				+ " [expected = ]" + expected + " fluctuation is too large, please immediately check. [date] "
				+ new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisSQpsDAlarm(String topic, String consumerId, long delay, long expected) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [senddelay = ]" + delay
				+ " [expected = ]" + expected + " is too long, please immediately check. [date] "
				+ new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisSAccuAlarm(String topic, String consumerId, long accumulation, long expected) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendaccumulation = ]"
				+ accumulation + " [expected = ]" + expected + " is too much, please immediately check. [date] "
				+ new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisAQpsPAlarm(String topic, String consumerId, long qpx) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisAQpsVAlarm(String topic, String consumerId, long qpx) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisAQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
				+ " [expected = ]" + expected + " fluctuation is too large, please immediately check. [date] "
				+ new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	@Override
	public void consumerIdStatisAQpsDAlarm(String topic, String consumerId, long delay, long expected) {
		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackdelay = ]" + delay
				+ " [expected = ]" + expected + " is too long, please immediately check. [date] "
				+ new Date().toString();
		sendAlarmByTopicAndConsumerId(topic, consumerId,"ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmLevelType.MAJOR);
	}

	private void sendAlarmSwallowDp(String title, String message, AlarmLevelType type) {
		sendAlarmByIp(ipCollectorService.getProducerServerIp(), title, message, type);
	}

	private void sendAlarmByIp(String ip, String title, String message, AlarmLevelType type) {
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		Set<String> ips = new HashSet<String>();
		ips.add(ip);
		fillReciever(ips, mobiles, emails);
		sendAll(mobiles, emails, title, message, type);
	}

	private void sendAlarmByProducerTopic(String topicName, String title, String message, AlarmLevelType type) {
		Set<String> ips = ipCollectorService.getProducerTopicIps(topicName);
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAll(mobiles, emails, title, message, type);
	}

	private void sendAlarmByTopicAndConsumerId(String topicName, String consumerId, String title, String message,
			AlarmLevelType type) {
		Set<String> ips = ipCollectorService.getTopicConsumerIdIps(topicName, consumerId);
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAll(mobiles, emails, title, message, type);
	}

	private void fillReciever(Set<String> ips, Set<String> mobiles, Set<String> emails) {
		if (ips == null || mobiles == null || emails == null) {
			return;
		}
		Iterator<String> iterator = ips.iterator();
		while (iterator.hasNext()) {
			String ip = iterator.next();
			if (!StringUtils.equals(ip, TOTAL_KEY)) {
				IPDesc ipDesc = ipDescManager.getIPDesc(ip);
				String strEmail = ipDesc.getEmail();
				String strDpMobile = ipDesc.getDpMobile();
				String strOpMobile = ipDesc.getOpMobile();
				String strOpEmail = ipDesc.getOpEmail();
				addElement(mobiles, strDpMobile);
				addElement(mobiles, strOpMobile);
				addElement(emails, strOpEmail);
				addElement(emails, strEmail);
			}
		}
	}

	private void sendAll(Set<String> mobiles, Set<String> emails, String title, String message, AlarmLevelType type) {
		alarmService.sendSms(mobiles, title, message, type);
		alarmService.sendMail(emails, title, message, type);
		alarmService.sendWeiXin(emails, title, message, type);
	}

	private void addElement(Set<String> mobiles, String strSource) {
		if (StringUtils.isBlank(strSource)) {
			return;
		}
		String[] elements = strSource.split(COMMA_SPLIT);
		if (elements != null) {
			for (String element : elements) {
				if (StringUtils.isNotBlank(element)) {
					mobiles.add(element);
				}
			}
		}
	}

}
