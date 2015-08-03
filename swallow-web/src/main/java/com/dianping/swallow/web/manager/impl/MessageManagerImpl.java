package com.dianping.swallow.web.manager.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateFormatUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.event.ConsumerIdEvent;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
import com.dianping.swallow.web.model.event.TopicEvent;
import com.dianping.swallow.web.service.AlarmMetaService;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SeqGeneratorService;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 上午11:11:58
 */
@Service("messageManager")
public class MessageManagerImpl implements MessageManager, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(MessageManagerImpl.class);

	private static final String TOTAL_KEY = "total";

	private static final String COMMA_SPLIT = ",";

	private static final String KEY_SPLIT = "&";

	private final Map<String, Long> producerServerAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> producerTopicAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerServerAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerTopicAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerIdAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<Integer, AlarmMeta> alarmMetas = new ConcurrentHashMap<Integer, AlarmMeta>();

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String IP_TEMPLATE = "{ip}";

	private static final String DATE_TEMPLATE = "{date}";

	private static final String CURRENTVALUE_TEMPLATE = "{currentValue}";

	private static final String EXPECTEDVALUE_TEMPLATE = "{expectedValue}";

	private static final String MASTERIP_TEMPLATE = "{masterIp}";

	private static final String SLAVEIP_TEMPLATE = "{slaveIp}";

	private static final String TOPIC_TEMPLATE = "{topic}";

	private static final String CONSUMERID_TEMPLATE = "{consumerId}";

	private static final String ALARM_RECIEVER_FILE_NAME = "swallow-alarm-reciever.properties";

	private static final String MOBILE_KEY = "mobile";

	private static final String EMAIL_KEY = "email";

	private Set<String> devMobiles;

	private Set<String> devEmails;

	private int interval = 120;// 秒

	private int delay = 5;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	@SuppressWarnings("unused")
	private ScheduledFuture<?> future = null;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private AlarmMetaService alarmMetaService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private SeqGeneratorService seqGeneratorService;

	private static final String ALARMEVENTID_CATEGORY = "alarmEventId";

	private static final TimeZone TIMEZONE = TimeZone.getTimeZone("GMT+8:00");

	@Override
	public void producerServerAlarm(ServerEvent event) {
		logger.info("ip {}   alarmType {}", event.getIp(), event.getAlarmType());
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isProducerServerAlarm(event.getIp(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, event.getIp());
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				logger.info("ip {}   alarmType number {}", event.getIp(), event.getAlarmType().getNumber());
				sendAlarmByIp(event.getIp(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void producerServerStatisAlarm(ServerStatisEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isProducerServerAlarm(event.getIp(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, event.getIp());
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(event.getCurrentValue()));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(event.getExpectedValue()));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(event.getIp(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void producerTopicStatisAlarm(TopicEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isProducerTopicAlarm(event.getTopicName(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, event.getTopicName());
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(event.getCurrentValue()));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(event.getExpectedValue()));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			}
			if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByProducerTopic(event.getTopicName(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerServerAlarm(ServerEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isConsumerServerAlarm(event.getIp(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, event.getIp());
				message = StringUtils.replace(message, MASTERIP_TEMPLATE, event.getIp());
				message = StringUtils.replace(message, SLAVEIP_TEMPLATE, event.getSlaveIp());
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(event.getIp(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerServerStatisAlarm(ServerStatisEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isConsumerServerAlarm(event.getIp(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, event.getIp());
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(event.getCurrentValue()));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(event.getExpectedValue()));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(event.getIp(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerTopicStatisAlarm(TopicEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isConsumerTopicAlarm(event.getTopicName(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, event.getTopicName());
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(event.getCurrentValue()));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(event.getExpectedValue()));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			}
			if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByConsumerTopic(event.getTopicName(), alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerIdStatisAlarm(ConsumerIdEvent event) {
		AlarmMeta alarmMeta = alarmMetas.get(event.getAlarmType().getNumber());
		if (alarmMeta != null && isConsumerIdAlarm(event.getTopicName(), event.getConsumerId(), alarmMeta)) {
			long eventId = seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
			event.setEventId(Long.toString(eventId));
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, event.getTopicName());
				message = StringUtils.replace(message, CONSUMERID_TEMPLATE, event.getConsumerId());
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(event.getCurrentValue()));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(event.getExpectedValue()));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, TIMEZONE));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(event.getAlarmType().getNumber()).setEventId(event.getEventId()).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			}
			if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByTopicAndConsumerId(event.getTopicName(), event.getConsumerId(), alarm, alarmMeta);
			}
		}

	}

	private void sendAlarmSwallowDp(Alarm alarm, AlarmMeta alarmMeta) {
		List<String> serverIps = ipCollectorService.getProducerServerIps();
		if (serverIps != null && serverIps.size() > 0) {
			sendAlarmByIp(serverIps.get(0), alarm, alarmMeta);
		}
	}

	private void sendAlarmByIp(String ip, Alarm alarm, AlarmMeta alarmMeta) {
		if (StringUtils.isNotBlank(ip)) {
			Set<String> mobiles = new HashSet<String>();
			Set<String> emails = new HashSet<String>();
			Set<String> ips = new HashSet<String>();
			ips.add(ip);
			fillReciever(ips, mobiles, emails);
			sendAlarm(mobiles, emails, alarm, alarmMeta);
		}

	}

	private void sendAlarmByProducerTopic(String topicName, Alarm alarm, AlarmMeta alarmMeta) {
		Set<String> ips = ipCollectorService.getProducerTopicIps(topicName);
		if (logger.isInfoEnabled()) {
			logger.info("topicName " + topicName + " ips " + ips);
		}
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void sendAlarmByConsumerTopic(String topicName, Alarm alarm, AlarmMeta alarmMeta) {
		Set<String> ips = ipCollectorService.getConsumerTopicIps(topicName);
		if (logger.isInfoEnabled()) {
			logger.info("topicName " + topicName + " ips " + ips);
		}
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void sendAlarmByTopicAndConsumerId(String topicName, String consumerId, Alarm alarm, AlarmMeta alarmMeta) {
		Set<String> ips = ipCollectorService.getTopicConsumerIdIps(topicName, consumerId);
		if (logger.isInfoEnabled()) {
			logger.info("topicName " + topicName + " consumerId " + consumerId + " ips " + ips);
		}
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void fillReciever(Set<String> ips, Set<String> mobiles, Set<String> emails) {
		if (ips == null || mobiles == null || emails == null) {
			return;
		}
		if (EnvUtil.isDev()) {
			if (devMobiles != null && devEmails != null) {
				mobiles.addAll(devMobiles);
				emails.addAll(devEmails);
			}
			return;
		}
		Iterator<String> iterator = ips.iterator();
		while (iterator.hasNext()) {
			String ip = iterator.next();
			if (!StringUtils.equals(ip, TOTAL_KEY)) {
				IPDesc ipDesc = ipDescManager.getIPDesc(ip);
				if (ipDesc == null) {
					logger.info("[fillReciever]cannot find {} related info from cmdb and db", ip);
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

	private void sendAlarm(Set<String> mobiles, Set<String> emails, Alarm alarm, AlarmMeta alarmMeta) {
		if (alarmMeta.getIsMailMode()) {
			alarmService.sendMail(emails, alarm);
		}
		if (alarmMeta.getIsSmsMode()) {
			alarmService.sendSms(mobiles, alarm);
		}
		if (alarmMeta.getIsWeiXinMode()) {
			alarmService.sendWeiXin(emails, alarm);
		}
	}

	private void addElement(Set<String> elementSet, String strSource) {
		if (StringUtils.isBlank(strSource)) {
			return;
		}
		String[] elements = strSource.split(COMMA_SPLIT);
		if (elements != null) {
			for (String element : elements) {
				if (StringUtils.isNotBlank(element)) {
					elementSet.add(element);
				}
			}
		}
	}

	private boolean isAlarm(Map<String, Long> alarms, String key, AlarmMeta alarmMeta) {
		long dValue = 0;
		if (alarms.containsKey(key)) {
			dValue = System.currentTimeMillis() - alarms.get(key).longValue();
			if (dValue > alarmMeta.getSendTimeSpan() * 60 * 1000) {
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

	private boolean isProducerServerAlarm(String ip, AlarmMeta alarmMeta) {
		String key = ip + KEY_SPLIT + alarmMeta.getType().getNumber();
		return isAlarm(producerServerAlarms, key, alarmMeta);
	}

	private boolean isProducerTopicAlarm(String topic, AlarmMeta alarmMeta) {
		String key = topic + KEY_SPLIT + alarmMeta.getType().getNumber();
		return isAlarm(producerTopicAlarms, key, alarmMeta);
	}

	private boolean isConsumerServerAlarm(String ip, AlarmMeta alarmMeta) {
		String key = ip + KEY_SPLIT + alarmMeta.getType().getNumber();
		return isAlarm(consumerServerAlarms, key, alarmMeta);
	}

	private boolean isConsumerTopicAlarm(String topic, AlarmMeta alarmMeta) {
		String key = topic + KEY_SPLIT + alarmMeta.getType().getNumber();
		return isAlarm(consumerTopicAlarms, key, alarmMeta);
	}

	private boolean isConsumerIdAlarm(String topic, String consumerId, AlarmMeta alarmMeta) {
		String key = topic + KEY_SPLIT + consumerId + KEY_SPLIT + alarmMeta.getType().getNumber();
		return isAlarm(consumerIdAlarms, key, alarmMeta);
	}

	private void initProperties() {
		if (!EnvUtil.isDev()) {
			return;
		}
		devMobiles = new HashSet<String>();
		devEmails = new HashSet<String>();
		try {
			InputStream in = MessageManagerImpl.class.getClassLoader().getResourceAsStream(ALARM_RECIEVER_FILE_NAME);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					String strMobile = StringUtils.trim(prop.getProperty(MOBILE_KEY));
					addElement(devMobiles, strMobile);
					String strEmail = StringUtils.trim(prop.getProperty(EMAIL_KEY));
					addElement(devEmails, strEmail);
				} finally {
					in.close();
				}
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", ALARM_RECIEVER_FILE_NAME);
			throw new RuntimeException(e);
		}
	}

	private void scheduleAlarmMetaTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doAlarmMetaTask();
					logger.info("[doAlarmMetaTask] scheduled load alarmMeta info.");
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, delay, interval, TimeUnit.SECONDS);
	}

	private void doAlarmMetaTask() {
		List<AlarmMeta> alarmMetaTemps = alarmMetaService.findByPage(0, AlarmType.values().length);
		if (alarmMetaTemps != null && alarmMetaTemps.size() > 0) {
			for (AlarmMeta alarmMeta : alarmMetaTemps) {
				alarmMetas.put(alarmMeta.getType().getNumber(), alarmMeta);
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initProperties();
		scheduleAlarmMetaTask();
	}

}