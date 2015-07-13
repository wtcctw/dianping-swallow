//package com.dianping.swallow.web.manager.impl;
//
//import java.util.Date;
//import java.util.Iterator;
//import java.util.Set;
//
//import org.codehaus.plexus.util.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.dianping.swallow.web.manager.AlarmManager;
//import com.dianping.swallow.web.model.alarm.AlarmType;
//import com.dianping.swallow.web.service.AlarmService;
//import com.dianping.swallow.web.service.IPCollectorService;
//
///**
// * 
// * @author qiyin
// *
// */
//@Service("alarmManager")
//public class AlarmManagerImpl implements AlarmManager {
//
//	@Autowired
//	private AlarmService alarmService;
//
//	@Autowired
//	private IPCollectorService ipConllectorService;
//
//	@Override
//	public void producerServiceAlarm(String ip) {
//		String message = "producer server is not work, please immediately repair. [date] " + new Date().toString();
//		alarmService.sendAll(ip, "ProducerServerService Alarm", message, AlarmType.CRITICAL);
//	}
//
//	@Override
//	public void producerSenderAlarm(String ip) {
//		String message = "producer server [ip] " + ip
//				+ " is not sender statistics data to web, please immediately repair. [date] " + new Date().toString();
//		alarmService.sendAll(ip, "ProducerServerSender Alarm", message, AlarmType.CRITICAL);
//	}
//
//	@Override
//	public void producerServerStatisQpsPAlarm(String serverIp, long qpx) {
//		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ProducerServerQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerServerStatisQpsVAlarm(String serverIp, long qpx) {
//		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx
//				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ProducerServerQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerServerStatisQpsFAlarm(String serverIp, long qpx, long expected) {
//		String message = "producer server [ip] " + serverIp + " [qpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ProducerServerQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerTopicStatisQpsPAlarm(String topic, long qpx) {
//		String message = "producer [topic] " + topic + " [qpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getProducerTopicIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerTopicStatisQpsVAlarm(String topic, long qpx) {
//		String message = "producer [topic] " + topic + " [qpx = ]" + qpx
//				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getProducerTopicIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerTopicStatisQpsFAlarm(String topic, long qpx, long expected) {
//		String message = "producer [topic] " + topic + " [qpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getProducerTopicIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void producerTopicStatisQpsDAlarm(String topic, long delay, long expected) {
//		String message = "producer [topic] " + topic + " [delay = ]" + delay + " [expected = ]" + expected
//				+ " is too long, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getProducerTopicIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ProducerTopicQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerPortAlarm(String masterIp, String slaveIp, boolean isBoth) {
//		String message = "";
//		if (isBoth) {
//			message = "consumer server port [masterIp] " + masterIp + " [slaveIp] " + slaveIp
//					+ "port both are opened, please immediately repair. [date] " + new Date().toString();
//		} else {
//			message = "consumer server port [masterIp] " + masterIp + " [slaveIp] " + slaveIp
//					+ " slave port is opened, please immediately repair. [date] " + new Date().toString();
//		}
//		alarmService.sendAll(masterIp, "ConsumerServerPort Alarm", message, AlarmType.CRITICAL);
//	}
//
//	@Override
//	public void consumerSenderAlarm(String ip) {
//
//		String message = "consumer server [ip] " + ip
//				+ " is not sender statistics data to web, please immediately repair. [date] " + new Date().toString();
//		alarmService.sendAll(ip, "ConsumerServerSender Alarm", message, AlarmType.CRITICAL);
//	}
//
//	@Override
//	public void consumerServerStatisSQpsPAlarm(String serverIp, long qpx) {
//		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerServerStatisSQpsVAlarm(String serverIp, long qpx) {
//		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx
//				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerServerStatisSQpsFAlarm(String serverIp, long qpx, long expected) {
//		String message = "consumer server [ip] " + serverIp + " [sendqpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerServerStatisAQpsPAlarm(String serverIp, long qpx) {
//		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerServerStatisAQpsVAlarm(String serverIp, long qpx) {
//		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx
//				+ " is lower than valley value, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerServerStatisAQpsFAlarm(String serverIp, long qpx, long expected) {
//		String message = "consumer server [ip] " + serverIp + " [ackqpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		alarmService.sendAll(serverIp, "ConsumerServerAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisSQpsPAlarm(String topic, long qpx) {
//		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisSQpsVAlarm(String topic, long qpx) {
//		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx
//				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisSQpsFAlarm(String topic, long qpx, long expected) {
//		String message = "consumer [topic] " + topic + " [sendqpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisSQpsDAlarm(String topic, long delay, long expected) {
//		String message = "consumer [topic] " + topic + " [senddelay = ]" + delay + " [expected = ]" + expected
//				+ " is too long, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisAQpsPAlarm(String topic, long qpx) {
//		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisAQpsVAlarm(String topic, long qpx) {
//		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx
//				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisAQpsFAlarm(String topic, long qpx, long expected) {
//		String message = "consumer [topic] " + topic + " [ackqpx = ]" + qpx + " [expected = ]" + expected
//				+ " fluctuation is too large, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerTopicStatisAQpsDAlarm(String topic, long delay, long expected) {
//		String message = "consumer [topic] " + topic + " [ackdelay = ]" + delay + " [expected = ]" + expected
//				+ " is too long, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerTopicAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisSQpsPAlarm(String topic, String consumerId, long qpx) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		String key = ipConllectorService.getTopicConsumerIdKey(topic, consumerId);
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(key);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisSQpsVAlarm(String topic, String consumerId, long qpx) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
//				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisSQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendqpx = ]" + qpx
//				+ " [expected = ]" + expected + " fluctuation is too large, please immediately check. [date] "
//				+ new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisSQpsDAlarm(String topic, String consumerId, long delay, long expected) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [senddelay = ]" + delay
//				+ " [expected = ]" + expected + " is too long, please immediately check. [date] "
//				+ new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisSAccuAlarm(String topic, String consumerId, long accumulation, long expected) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [sendaccumulation = ]"
//				+ accumulation + " [expected = ]" + expected + " is too much, please immediately check. [date] "
//				+ new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdSendQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisAQpsPAlarm(String topic, String consumerId, long qpx) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
//				+ " is higher than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisAQpsVAlarm(String topic, String consumerId, long qpx) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
//				+ " is lower than peak value, please immediately check. [date] " + new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisAQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackqpx = ]" + qpx
//				+ " [expected = ]" + expected + " fluctuation is too large, please immediately check. [date] "
//				+ new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	@Override
//	public void consumerIdStatisAQpsDAlarm(String topic, String consumerId, long delay, long expected) {
//		String message = "consumer [topic] " + topic + " [consumerId] " + consumerId + " [ackdelay = ]" + delay
//				+ " [expected = ]" + expected + " is too long, please immediately check. [date] "
//				+ new Date().toString();
//		String ip = ipConllectorService.getTopicConsumerIdIps().get(topic);
//		if (StringUtils.isNotBlank(ip)) {
//			alarmService.sendAll(ip, "ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//		}
//		sendAlarmSwallowDp("ConsumerConsumerIdAckQps Alarm", message, AlarmType.MAJOR);
//	}
//
//	private void sendAlarmSwallowDp(String title, String message, AlarmType type) {
//		alarmService.sendAll(ipConllectorService.getProducerServerIp(), title, message, type);
//	}
//
//	private void getReciver(Set<String> ips, Set<String> mobiles, Set<String> emails) {
//		if (ips == null || mobiles == null || emails == null) {
//			return;
//		}
//		Iterator<String> iterator = ips.iterator();
//		while(iterator)
//
//	}
//
//	private void sendAll(Set<String> mobiles, Set<String> emails, String title, String message, AlarmType type) {
//		alarmService.sendSms(mobiles, title, message, type);
//		alarmService.sendMail(emails, title, message, type);
//		alarmService.sendWeiXin(emails, title, message, type);
//	}
//
//}
