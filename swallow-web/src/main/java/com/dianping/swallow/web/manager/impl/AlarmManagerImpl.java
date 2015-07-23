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

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.SendType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmMetaService;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SeqGeneratorService;

@Service("alarmManager")
public class AlarmManagerImpl implements AlarmManager, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmManagerImpl.class);

	private static final String TOTAL_KEY = "total";

	private static final String COMMA_SPLIT = ",";

	private static final String KEY_SPLIT = "&";

	private final Map<String, Long> producerServerAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> producerTopicAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerServerAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerTopicAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<String, Long> consumerIdAlarms = new ConcurrentHashMap<String, Long>();

	private final Map<Integer, AlarmMeta> alarmMetas = new ConcurrentHashMap<Integer, AlarmMeta>();

	private static final String env;

	private static final String DEV_ENV = "dev";

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

	static {
		env = EnvZooKeeperConfig.getEnv().trim();
	}

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

	private TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");

	@Override
	public void producerServerAlarm(String ip, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isProducerServerAlarm(ip, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, ip);
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(ip, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void producerServerStatisAlarm(String ip, long currentValue, long expectedValue, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isProducerServerAlarm(ip, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, ip);
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(currentValue));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(expectedValue));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(ip, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void producerTopicStatisAlarm(String topic, long currentValue, long expectedValue, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isProducerTopicAlarm(topic, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, topic);
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(currentValue));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(expectedValue));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			} else if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByProducerTopic(topic, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerServerAlarm(String masterIp, String slaveIp, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isConsumerServerAlarm(masterIp, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, masterIp);
				message = StringUtils.replace(message, MASTERIP_TEMPLATE, masterIp);
				message = StringUtils.replace(message, SLAVEIP_TEMPLATE, slaveIp);
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(masterIp, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerServerStatisAlarm(String ip, long currentValue, long expectedValue, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isConsumerServerAlarm(ip, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, IP_TEMPLATE, ip);
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(currentValue));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(expectedValue));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			if (alarmMeta.getIsSendSwallow()) {
				Alarm alarm = new Alarm();
				alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				sendAlarmByIp(ip, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerTopicStatisAlarm(String topic, long currentValue, long expectedValue, AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isConsumerTopicAlarm(topic, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, topic);
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(currentValue));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(expectedValue));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			} else if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByConsumerTopic(topic, alarm, alarmMeta);
			}
		}
	}

	@Override
	public void consumerIdStatisAlarm(String topic, String consumerId, long currentValue, long expectedValue,
			AlarmType alarmType) {
		AlarmMeta alarmMeta = alarmMetas.get(alarmType.getNumber());
		if (alarmMeta != null && isConsumerIdAlarm(topic, consumerId, alarmMeta)) {
			String message = alarmMeta.getAlarmTemplate();
			if (StringUtils.isNotBlank(message)) {
				message = StringUtils.replace(message, TOPIC_TEMPLATE, topic);
				message = StringUtils.replace(message, CONSUMERID_TEMPLATE, topic);
				message = StringUtils.replace(message, CURRENTVALUE_TEMPLATE, Long.toString(currentValue));
				message = StringUtils.replace(message, EXPECTEDVALUE_TEMPLATE, Long.toString(expectedValue));
				message = StringUtils.replace(message, DATE_TEMPLATE,
						DateFormatUtils.format(new Date(), DATE_PATTERN, timeZone));
			}
			Alarm alarm = new Alarm();
			alarm.setNumber(alarmType.getNumber()).setEventId(Long.toString(getSeqGeneratorId())).setBody(message)
					.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
			if (alarmMeta.getIsSendSwallow()) {
				sendAlarmSwallowDp(alarm, alarmMeta);
			} else if (alarmMeta.getIsSendBusiness()) {
				sendAlarmByTopicAndConsumerId(topic, consumerId, alarm, alarmMeta);
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
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void sendAlarmByConsumerTopic(String topicName, Alarm alarm, AlarmMeta alarmMeta) {

		Set<String> ips = ipCollectorService.getConsumerTopicIps(topicName);
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void sendAlarmByTopicAndConsumerId(String topicName, String consumerId, Alarm alarm, AlarmMeta alarmMeta) {
		Set<String> ips = ipCollectorService.getTopicConsumerIdIps(topicName, consumerId);
		Set<String> mobiles = new HashSet<String>();
		Set<String> emails = new HashSet<String>();
		fillReciever(ips, mobiles, emails);
		sendAlarm(mobiles, emails, alarm, alarmMeta);
	}

	private void fillReciever(Set<String> ips, Set<String> mobiles, Set<String> emails) {
		if (ips == null || mobiles == null || emails == null) {
			return;
		}
		if (env.equals(DEV_ENV)) {
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
			alarm.setSendType(SendType.MAIL);
			alarmService.sendMail(emails, alarm);
		}
		if (alarmMeta.getIsSmsMode()) {
			alarm.setSendType(SendType.SMS);
			alarmService.sendSms(mobiles, alarm);
		}
		if (alarmMeta.getIsWeiXinMode()) {
			alarm.setSendType(SendType.WEIXIN);
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

	private long getSeqGeneratorId() {
		return seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
	}

	private void initProperties() {
		if (env.equals(DEV_ENV)) {
			devMobiles = new HashSet<String>();
			devEmails = new HashSet<String>();
			try {
				InputStream in = AlarmManagerImpl.class.getClassLoader().getResourceAsStream(ALARM_RECIEVER_FILE_NAME);
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
	}

	private void scheduleAlarmMetaTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doAlarmMetaTask();
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