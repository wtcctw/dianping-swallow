package com.dianping.swallow.web.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.manager.AlarmManager0;
import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmMetaService;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.manager.IPDescManager;

/**
 * 
 * @author qiyin
 *
 */
//@Service("alarmManager")
public class AlarmManagerImpl0 implements AlarmManager0 {

	private static final String TOTAL_KEY = "total";

	private static final String COMMA_SPLIT = ",";

	private static final String KEY_SPLIT = "&";

	private static final long ALARM_INTERVAL = 5 * 60 * 1000;

	private final Map<String, Long> producerServerAlarms = new HashMap<String, Long>();

	private final Map<String, Long> producerTopicAlarms = new HashMap<String, Long>();

	private final Map<String, Long> consumerServerAlarms = new HashMap<String, Long>();

	private final Map<String, Long> consumerTopicAlarms = new HashMap<String, Long>();

	private final Map<String, Long> consumerIdAlarms = new HashMap<String, Long>();

	private static final String env;

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final boolean ISTEST = true;

	static {
		env = EnvZooKeeperConfig.getEnv().trim();
	}

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private IPDescManager ipDescManager;
	
	@Autowired
	private AlarmMetaService alarmMetaService;

	@Override
	public void producerServiceAlarm(String ip) {
		String message = "生产服务器[IP]" + ip + "不能访问pigeon健康监测页面，可能宕机。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN)
				+ "]";
		if (isProducerServerAlarm(ip, AlarmType.PRODUCER_SERVER_PIGEON_SERVICE)) {
			int number = AlarmType.PRODUCER_SERVER_PIGEON_SERVICE.getNumber();
			sendAlarmByIp(ip, "[" + Integer.toString(number) + "]" + "生产服务器服务告警", message, AlarmLevelType.CRITICAL);
		}
	}

	@Override
	public void producerSenderAlarm(String ip) {
		String message = "生产服务器[IP]" + ip + "未发送统计数据到管理端，可能宕机。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN)
				+ "]";
		if (isProducerServerAlarm(ip, AlarmType.PRODUCER_SERVER_SENDER)) {
			int number = AlarmType.PRODUCER_SERVER_SENDER.getNumber();
			sendAlarmByIp(ip, "[" + Integer.toString(number) + "]" + "生产服务器SENDER告警", message, AlarmLevelType.CRITICAL);
		}
	}

	@Override
	public void producerServerStatisQpsPAlarm(String serverIp, long qpx, long expected) {
		String message = "生产服务器[IP]" + serverIp + "，[QPS]" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_QPS_PEAK)) {
			int number = AlarmType.PRODUCER_SERVER_QPS_PEAK.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "生产服务器QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerServerStatisQpsVAlarm(String serverIp, long qpx, long expected) {
		String message = "生产服务器[IP]" + serverIp + "，[QPS]" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_QPS_VALLEY)) {
			int number = AlarmType.PRODUCER_SERVER_QPS_VALLEY.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "生产服务器QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerServerStatisQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "生产服务器[IP]" + serverIp + "，[QPS]" + qpx + "与历史同期值" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_QPS_FLUCTUATION)) {
			int number = AlarmType.PRODUCER_SERVER_QPS_FLUCTUATION.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "生产服务器QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerTopicStatisQpsPAlarm(String topic, long qpx, long expected) {
		String message = "生产客户端[TOPIC]" + topic + "，[QPS]" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerTopicAlarm(topic, AlarmType.PRODUCER_TOPIC_QPS_PEAK)) {
			int number = AlarmType.PRODUCER_TOPIC_QPS_PEAK.getNumber();
			sendAlarmByProducerTopic(topic, "[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerTopicStatisQpsVAlarm(String topic, long qpx, long expected) {
		String message = "生产客户端[TOPIC]" + topic + "，[QPS]" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerTopicAlarm(topic, AlarmType.PRODUCER_TOPIC_QPS_VALLEY)) {
			int number = AlarmType.PRODUCER_TOPIC_QPS_VALLEY.getNumber();
			sendAlarmByProducerTopic(topic, "[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerTopicStatisQpsFAlarm(String topic, long qpx, long expected) {
		String message = "生产客户端[TOPIC]" + topic + " [QPS]" + qpx + "与历史同期值" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerTopicAlarm(topic, AlarmType.PRODUCER_TOPIC_QPS_FLUCTUATION)) {
			int number = AlarmType.PRODUCER_TOPIC_QPS_FLUCTUATION.getNumber();
			sendAlarmByProducerTopic(topic, "[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "生产端TOPIC QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void producerTopicStatisQpsDAlarm(String topic, long delay, long expected) {
		String message = "生产客户端[TOPIC]" + topic + "延时" + delay + "s大于阈值" + expected + "s。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isProducerTopicAlarm(topic, AlarmType.PRODUCER_TOPIC_MESSAGE_DELAY)) {
			int number = AlarmType.PRODUCER_TOPIC_MESSAGE_DELAY.getNumber();
			sendAlarmByProducerTopic(topic, "[" + Integer.toString(number) + "]" + "生产端TOPIC DELAY告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "生产端TOPIC 延时告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerPortAlarm(String masterIp, String slaveIp, boolean isMasterOpen, boolean isSlaveOpen) {
		String message = "";
		if (isMasterOpen && isSlaveOpen) {
			message = "消费服务器[MASTER IP]" + masterIp + "[SLAVE IP]" + slaveIp + "，MASTER SLAVE端口都处于打开状态。" + "["
					+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
			if (isConsumerServerAlarm(masterIp, AlarmType.CONSUMER_SERVER_BOTHPORT_OPENED)) {
				int number = AlarmType.CONSUMER_SERVER_BOTHPORT_OPENED.getNumber();
				sendAlarmByIp(masterIp, "[" + Integer.toString(number) + "]" + "消费服务器PORT告警", message,
						AlarmLevelType.CRITICAL);
			}
		} else if (!isMasterOpen && isSlaveOpen) {
			message = "消费服务器[MASTER IP]" + masterIp + "[SLAVE IP]" + slaveIp + "，SLAVE端口处于打开状态。" + "["
					+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
			if (isConsumerServerAlarm(masterIp, AlarmType.CONSUMER_SERVER_SLAVEPORT_OPENED)) {
				int number = AlarmType.CONSUMER_SERVER_SLAVEPORT_OPENED.getNumber();
				sendAlarmByIp(masterIp, "[" + Integer.toString(number) + "]" + "消费服务器PORT告警", message,
						AlarmLevelType.CRITICAL);
			}
		} else if (!isMasterOpen && !isSlaveOpen) {
			message = "消费服务器[MASTER IP]" + masterIp + "[SLAVE IP]" + slaveIp + "，MASTER SLAVE端口都处于未打开状态。" + "["
					+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
			if (isConsumerServerAlarm(masterIp, AlarmType.CONSUMER_SERVER_BOTHPORT_UNOPENED)) {
				int number = AlarmType.CONSUMER_SERVER_BOTHPORT_UNOPENED.getNumber();
				sendAlarmByIp(masterIp, "[" + Integer.toString(number) + "]" + "消费服务器PORT告警", message,
						AlarmLevelType.CRITICAL);
			}
		}

	}

	@Override
	public void consumerSenderAlarm(String ip) {
		String message = "消费服务器[IP]" + ip + "未发送统计数据到管理端，可能宕机。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN)
				+ "]";
		if (isConsumerServerAlarm(ip, AlarmType.CONSUMER_SERVER_SENDER)) {
			int number = AlarmType.CONSUMER_SERVER_SENDER.getNumber();
			sendAlarmByIp(ip, "[" + Integer.toString(number) + "]" + "消费服务器SENDER告警", message, AlarmLevelType.CRITICAL);
		}
	}

	@Override
	public void consumerServerStatisSQpsPAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + "发送QPS" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_SENDQPS_PEAK)) {
			int number = AlarmType.CONSUMER_SERVER_SENDQPS_PEAK.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器发送QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerServerStatisSQpsVAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + "发送QPS" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_SENDQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_SERVER_SENDQPS_VALLEY.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器发送QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerServerStatisSQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + " 发送QPS" + qpx + "与历史同期值" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_SENDQPS_FLUCTUATION)) {
			int number = AlarmType.CONSUMER_SERVER_SENDQPS_FLUCTUATION.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器发送QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerServerStatisAQpsPAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + "确认QPS" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_ACKQPS_PEAK)) {
			int number = AlarmType.CONSUMER_SERVER_ACKQPS_PEAK.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器确认QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerServerStatisAQpsVAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + "确认QPS" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_ACKQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_SERVER_ACKQPS_VALLEY.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器确认QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerServerStatisAQpsFAlarm(String serverIp, long qpx, long expected) {
		String message = "消费服务器[IP]" + serverIp + "确认QPS" + qpx + "与历史同期" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerServerAlarm(serverIp, AlarmType.CONSUMER_SERVER_ACKQPS_FLUCTUATION)) {
			int number = AlarmType.CONSUMER_SERVER_ACKQPS_FLUCTUATION.getNumber();
			sendAlarmByIp(serverIp, "[" + Integer.toString(number) + "]" + "消费服务器确认QPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisSQpsPAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "发送QPS" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_SENDQPS_PEAK)) {
			int number = AlarmType.CONSUMER_TOPIC_SENDQPS_PEAK.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisSQpsVAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "发送QPS" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_SENDQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_TOPIC_SENDQPS_VALLEY.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisSQpsFAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "发送QPS" + qpx + "与历史同期值" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_SENDQPS_FLUCTUATION)) {
			int number = AlarmType.CONSUMER_TOPIC_SENDQPS_FLUCTUATION.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisSQpsDAlarm(String topic, long delay, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "发送延时" + delay + "s延时大于阈值" + expected + "s。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_SENDMESSAGE_DELAY)) {
			int number = AlarmType.CONSUMER_TOPIC_SENDMESSAGE_DELAY.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC发送延时告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC SENDDELAY告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisAQpsPAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "确认QPS" + qpx + "高于峰值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_ACKQPS_PEAK)) {
			int number = AlarmType.CONSUMER_TOPIC_ACKQPS_PEAK.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC确认QPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC ACKQPS告警", message,
					AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisAQpsVAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "确认QPS" + qpx + "低于谷值" + expected + "。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_ACKQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_TOPIC_ACKQPS_VALLEY.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC ACKQPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC确认QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisAQpsFAlarm(String topic, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "确认QPS" + qpx + "与历史同期值" + expected + "波动较大。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_ACKQPS_FLUCTUATION)) {
			int number = AlarmType.CONSUMER_TOPIC_ACKQPS_FLUCTUATION.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC ACKQPS告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC确认QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerTopicStatisAQpsDAlarm(String topic, long delay, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "确认延时s" + delay + "延时大于阈值" + expected + "s。" + "["
				+ DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerTopicAlarm(topic, AlarmType.CONSUMER_TOPIC_ACKMESSAGE_DELAY)) {
			int number = AlarmType.CONSUMER_TOPIC_ACKMESSAGE_DELAY.getNumber();
			sendAlarmByConsumerTopic(topic, "[" + Integer.toString(number) + "]" + "消费端TOPIC ACKDELAY告警", message,
					AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端TOPIC确认延时告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisSQpsPAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "发送QPS" + qpx + "高于峰值" + expected + "。"
				+ "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_SENDQPS_PEAK)) {
			int number = AlarmType.CONSUMER_CONSUMERID_SENDQPS_PEAK.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端发送QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisSQpsVAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "发送QPS" + qpx + "低于谷值" + expected + "。"
				+ "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_SENDQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_CONSUMERID_SENDQPS_VALLEY.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端发送QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisSQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "[发送QPS]" + qpx + "与历史同期值" + expected
				+ "波动较大。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_SENDQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_CONSUMERID_SENDQPS_VALLEY.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端发送QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端发送QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisSQpsDAlarm(String topic, String consumerId, long delay, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "发送延时" + delay + "s延时大于阈值" + expected
				+ "s。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_DELAY)) {
			int number = AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_DELAY.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费客户端发送延时告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端发送延时告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisSAccuAlarm(String topic, String consumerId, long accumulation, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "消息累积" + accumulation + "累积大于阈值"
				+ expected + "。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION)) {
			int number = AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端消息累积告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端消息累积告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisAQpsPAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "确认QPS" + qpx + "高于峰值" + expected + "。"
				+ "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_ACKQPS_PEAK)) {
			int number = AlarmType.CONSUMER_CONSUMERID_ACKQPS_PEAK.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端确认QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端确认QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisAQpsVAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "确认QPS" + qpx + "低于谷值" + expected + "。"
				+ "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_ACKQPS_VALLEY)) {
			int number = AlarmType.CONSUMER_CONSUMERID_ACKQPS_VALLEY.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端确认QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端确认QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisAQpsFAlarm(String topic, String consumerId, long qpx, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "确认QPS" + qpx + "与历史同期值" + expected
				+ "波动较大。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION)) {
			int number = AlarmType.CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端确认QPS告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端确认QPS告警", message, AlarmLevelType.MAJOR);
		}
	}

	@Override
	public void consumerIdStatisAQpsDAlarm(String topic, String consumerId, long delay, long expected) {
		String message = "消费客户端[TOPIC]" + topic + "[CONSUMERID]" + consumerId + "确认延时" + delay + "s延时大于阈值" + expected
				+ "s。" + "[" + DateFormatUtils.format(new Date(), DATE_PATTERN) + "]";
		if (isConsumerIdAlarm(topic, consumerId, AlarmType.CONSUMER_CONSUMERID_ACKMESSAGE_DELAY)) {
			int number = AlarmType.CONSUMER_CONSUMERID_ACKMESSAGE_DELAY.getNumber();
			sendAlarmByTopicAndConsumerId(topic, consumerId, "[" + Integer.toString(number) + "]" + "消费端确认延时告警",
					message, AlarmLevelType.MAJOR);
			sendAlarmSwallowDp("[" + Integer.toString(number) + "]" + "消费端确认延时告警", message, AlarmLevelType.MAJOR);
		}
	}

	private void sendAlarmSwallowDp(String title, String message, AlarmLevelType type) {
		List<String> serverIps = ipCollectorService.getProducerServerIps();
		if (serverIps != null && serverIps.size() > 0) {
			sendAlarmByIp(serverIps.get(0), title, message, type);
		}
	}

	private void sendAlarmByIp(String ip, String title, String message, AlarmLevelType type) {
		if (ISTEST) {
			List<String> serverIps = ipCollectorService.getProducerServerIps();
			if (serverIps != null && serverIps.size() > 0) {
				ip = serverIps.get(0);
			}
		}
		if (StringUtils.isNotBlank(ip)) {
			Set<String> mobiles = new HashSet<String>();
			Set<String> emails = new HashSet<String>();
			Set<String> ips = new HashSet<String>();
			ips.add(ip);
			fillReciever(ips, mobiles, emails);
			fillRecieverDev(mobiles, emails);
			sendAll(mobiles, emails, title, message, type);
		}

	}

	private void sendAlarmByProducerTopic(String topicName, String title, String message, AlarmLevelType type) {
		if (ISTEST) {
			// sendAlarmSwallowDp(title, message, type);
		} else {
			Set<String> ips = ipCollectorService.getProducerTopicIps(topicName);
			Set<String> mobiles = new HashSet<String>();
			Set<String> emails = new HashSet<String>();
			fillReciever(ips, mobiles, emails);
			// fillRecieverDev(mobiles, emails);
			sendAll(mobiles, emails, title, message, type);
		}
	}

	private void sendAlarmByConsumerTopic(String topicName, String title, String message, AlarmLevelType type) {
		if (ISTEST) {
			// sendAlarmSwallowDp(title, message, type);
		} else {
			Set<String> ips = ipCollectorService.getConsumerTopicIps(topicName);
			Set<String> mobiles = new HashSet<String>();
			Set<String> emails = new HashSet<String>();
			fillReciever(ips, mobiles, emails);
			// fillRecieverDev(mobiles, emails);
			sendAll(mobiles, emails, title, message, type);
		}
	}

	private void sendAlarmByTopicAndConsumerId(String topicName, String consumerId, String title, String message,
			AlarmLevelType type) {
		if (ISTEST) {
			// sendAlarmSwallowDp(title, message, type);
		} else {
			Set<String> ips = ipCollectorService.getTopicConsumerIdIps(topicName, consumerId);
			Set<String> mobiles = new HashSet<String>();
			Set<String> emails = new HashSet<String>();
			fillReciever(ips, mobiles, emails);
			// fillRecieverDev(mobiles, emails);
			sendAll(mobiles, emails, title, message, type);
		}
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
				if (ipDesc == null) {
					continue;
				}
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

	private boolean isAlarm(Map<String, Long> alarms, String key) {
		long dValue = 0;
		if (alarms.containsKey(key)) {
			dValue = System.currentTimeMillis() - alarms.get(key).longValue();
			if (dValue > ALARM_INTERVAL) {
				alarms.put(key, System.currentTimeMillis());
				return true;
			} else {
				return false;
			}
		} else {
			alarms.put(key, System.currentTimeMillis());
			return true;
		}
	}

	private boolean isProducerServerAlarm(String ip, AlarmType alarmType) {
		String key = ip + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(producerServerAlarms, key);
	}

	private boolean isProducerTopicAlarm(String topic, AlarmType alarmType) {
		String key = topic + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(producerTopicAlarms, key);
	}

	private boolean isConsumerServerAlarm(String ip, AlarmType alarmType) {
		String key = ip + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(consumerServerAlarms, key);
	}

	private boolean isConsumerTopicAlarm(String topic, AlarmType alarmType) {
		String key = topic + KEY_SPLIT + alarmType.hashCode();
		return isAlarm(consumerTopicAlarms, key);
	}

	private boolean isConsumerIdAlarm(String topic, String consumerId, AlarmType alarmType) {
		String key = topic + KEY_SPLIT + consumerId + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(consumerIdAlarms, key);
	}

	private void fillRecieverDev(Set<String> mobiles, Set<String> emails) {
		if (env.equals("dev")) {
			mobiles.clear();
			emails.clear();
			mobiles.add("13162757679");
			emails.add("qi.yin@dianping.com");
		}
	}

}
