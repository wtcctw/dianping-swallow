package com.dianping.swallow.web.model.event;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.web.alarmer.container.AlarmMetaContainer;
import com.dianping.swallow.web.manager.AlarmReceiverManager;
import com.dianping.swallow.web.manager.AlarmReceiverManager.AlarmReceiver;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:12:56
 */
public abstract class Event {

	protected static Logger logger = LoggerFactory.getLogger(Event.class);

	protected static final String KEY_SPLIT = "&";

	private static final String MOBILE_KEY = "mobile";

	private static final String EMAIL_KEY = "email";

	private static final String COMMA_SPLIT = ",";

	private static Set<String> devMobiles;

	private static Set<String> devEmails;

	private static final String ALARM_RECIEVER_FILE_NAME = "swallow-alarm-reciever.properties";

	private static final long timeUnit = 60 * 1000;

	static {
		initProperties();
	}

	private AlarmService alarmService;

	protected AlarmReceiverManager receiverManager;

	private AlarmMetaContainer alarmMetaContainer;

	private long eventId;

	private Date createTime;

	private AlarmType alarmType;

	private EventType eventType;

	public Date getCreateTime() {
		return createTime;
	}

	public Event setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public long getEventId() {
		return eventId;
	}

	public Event setEventId(long eventId) {
		this.eventId = eventId;
		return this;
	}

	public AlarmType getAlarmType() {
		return alarmType;
	}

	public Event setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
		return this;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Event setEventType(EventType eventType) {
		this.eventType = eventType;
		return this;
	}

	public void setAlarmService(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	public void setAlarmMetaContainer(AlarmMetaContainer alarmMetaContainer) {
		this.alarmMetaContainer = alarmMetaContainer;
	}

	public void setAlarmReceiverManager(AlarmReceiverManager receiverManager) {
		this.receiverManager = receiverManager;
	}

	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", createTime=" + createTime + ", alarmType=" + alarmType + ", eventType="
				+ eventType + "]";
	}

	public abstract void alarm();

	public abstract String getMessage(String template);

	public abstract String getRelated();

	protected String getSubRelated() {
		return StringUtils.EMPTY;
	}

	public abstract RelatedType getRelatedType();

	public abstract boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta);

	public abstract AlarmReceiver getRelatedReceiver();

	public void sendMessage(AlarmType alarmType) {
		logger.info("[sendMessage] AlarmType {}. ", alarmType);
		AlarmMeta alarmMeta = alarmMetaContainer.getAlarmMeta(alarmType.getNumber());
		if (alarmMeta != null) {
			if (isSendAlarm(alarmType, alarmMeta)) {
				if (!(alarmMeta.getIsMailMode() || alarmMeta.getIsSmsMode() || alarmMeta.getIsWeiXinMode())
						|| !(alarmMeta.getIsSendBusiness() || alarmMeta.getIsSendSwallow())) {
					logger.error("[sendMessage] as alarmMeta, no need to send message.metaId {}.",
							alarmType.getNumber());
					return;
				}
				long eventId = getNextSeq();
				logger.info("[sendMessage] eventId {}", eventId);
				Alarm alarm = new Alarm();
				alarm.setNumber(alarmType.getNumber()).setEventId(eventId)
						.setBody(getMessage(alarmMeta.getAlarmTemplate())).setRelated(getRelated())
						.setSubRelated(getSubRelated()).setRelatedType(getRelatedType())
						.setTitle(alarmMeta.getAlarmTitle()).setType(alarmMeta.getLevelType());
				AlarmReceiver alarmReceiver = fillReciever(alarmMeta);
				sendAlarm(alarmReceiver, alarm, alarmMeta);
			}
		} else {
			logger.error("[sendMessage] cannot find related alarmMeta. metaId {}. ", alarmType.getNumber());
		}
	}

	protected boolean isAlarm(Map<String, AlarmRecord> alarms, String key, AlarmMeta alarmMeta) {
		AlarmRecord alarmRecord = new AlarmRecord().setCheckAlarmTime(System.currentTimeMillis());
		if (alarms.containsKey(key)) {

			AlarmRecord lastAlarmRecord = alarms.get(key);
			long dAlarmValue = System.currentTimeMillis() - lastAlarmRecord.getLastAlarmTime();
			long dCheckValue = System.currentTimeMillis() - lastAlarmRecord.getCheckAlarmTime();
			int spanBase = getTimeSpan(alarmMeta.getDaySpanBase(), alarmMeta.getNightSpanBase());

			if (0 < dCheckValue && dCheckValue < timeUnit) {
				long currentTimeSpan = spanBase * lastAlarmRecord.getAlarmCount() * timeUnit;
				long maxTimeSpan = alarmMeta.getMaxTimeSpan() * timeUnit;
				long timeSpan = currentTimeSpan > maxTimeSpan ? maxTimeSpan : currentTimeSpan;

				if (dAlarmValue > timeSpan) {
					alarmRecord.setAlarmCount(lastAlarmRecord.getAlarmCount() + 1).setLastAlarmTime(
							System.currentTimeMillis());
					alarms.put(key, alarmRecord);
					return true;
				} else {
					alarmRecord.setAlarmCount(lastAlarmRecord.getAlarmCount()).setLastAlarmTime(
							lastAlarmRecord.getLastAlarmTime());
					alarms.put(key, alarmRecord);
					return false;
				}

			} else {
				if (dAlarmValue > spanBase * timeUnit) {
					alarmRecord.setAlarmCount(lastAlarmRecord.getAlarmCount() + 1).setLastAlarmTime(
							System.currentTimeMillis());
					alarms.put(key, alarmRecord);
					return true;
				} else {
					alarmRecord.setAlarmCount(lastAlarmRecord.getAlarmCount()).setLastAlarmTime(
							lastAlarmRecord.getLastAlarmTime());
					alarms.put(key, alarmRecord);
					return false;
				}
			}
		} else {

			alarmRecord.setAlarmCount(1).setLastAlarmTime(System.currentTimeMillis());
			alarms.put(key, alarmRecord);
			return true;
		}
	}

	protected int getTimeSpan(int daySpanRatio, int nightSpanRatio) {
		int hour = DateUtil.getCurrentHour();
		if (7 < hour && hour < 18) {
			return daySpanRatio;
		} else {
			return nightSpanRatio;
		}
	}

	protected long getNextSeq() {
		return alarmService.getNextEventId();
	}

	private void sendAlarm(AlarmReceiver alarmReceiver, Alarm alarm, AlarmMeta alarmMeta) {
		if (alarmReceiver == null) {
			logger.error("[sendAlarm] eventId {} no receiver.", alarm.getEventId());
		} else {
			alarmReceiver = new AlarmReceiver();
		}
		if (alarmMeta.getIsMailMode()) {
			alarmService.sendMail(alarmReceiver.getEmails(), alarm);
		}
		if (alarmMeta.getIsSmsMode()) {
			alarmService.sendSms(alarmReceiver.getMobiles(), alarm);
		}
		if (alarmMeta.getIsWeiXinMode()) {
			alarmService.sendWeiXin(alarmReceiver.getEmails(), alarm);
		}
		alarmService.insert(alarm);
	}

	private AlarmReceiver fillReciever(AlarmMeta alarmMeta) {
		AlarmReceiver receiver = null;
		if (EnvUtil.isDev()) {
			if (devMobiles != null && devEmails != null) {
				receiver = new AlarmReceiver(devEmails, devMobiles);
			}
			return receiver;
		} else {
			if (alarmMeta.getIsSendSwallow()) {
				receiver = receiverManager.getSwallowReceiver();
			}
			if (alarmMeta.getIsSendBusiness()) {
				if (receiver == null) {
					receiver = getRelatedReceiver();
				} else {
					receiver.addAlarmReceiver(getRelatedReceiver());
				}
			}
		}

		return receiver;
	}

	private static void addElement(Set<String> elementSet, String strSource) {
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

	private static void initProperties() {
		if (!EnvUtil.isDev()) {
			return;
		}
		devMobiles = new HashSet<String>();
		devEmails = new HashSet<String>();
		try {
			InputStream in = Event.class.getClassLoader().getResourceAsStream(ALARM_RECIEVER_FILE_NAME);
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
