package com.dianping.swallow.web.model.event;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.web.container.AlarmMetaContainer;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SeqGeneratorService;

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

	private static final String TOTAL_KEY = "total";

	private static final String COMMA_SPLIT = ",";

	private static Set<String> devMobiles;

	private static Set<String> devEmails;

	private static final String ALARMEVENTID_CATEGORY = "alarmEventId";

	private static final String ALARM_RECIEVER_FILE_NAME = "swallow-alarm-reciever.properties";

	static {
		initProperties();
	}

	private AlarmService alarmService;

	private IPDescManager ipDescManager;

	private AlarmMetaContainer alarmMetaContainer;

	protected IPCollectorService ipCollectorService;

	private SeqGeneratorService seqGeneratorService;

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

	public void setIPDescManager(IPDescManager ipDescManager) {
		this.ipDescManager = ipDescManager;
	}

	public void setIPCollectorService(IPCollectorService ipCollectorService) {
		this.ipCollectorService = ipCollectorService;
	}

	public void setSeqGeneratorService(SeqGeneratorService seqGeneratorService) {
		this.seqGeneratorService = seqGeneratorService;
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

	public abstract boolean isSendAlarm(AlarmType alarmType, int timeSpan);

	public abstract Set<String> getRelatedIps();

	public void sendMessage(AlarmType alarmType) {
		logger.error("[sendMessage] AlarmType {}. ", alarmType);
		AlarmMeta alarmMeta = alarmMetaContainer.getAlarmMeta(alarmType.getNumber());
		if (alarmMeta != null) {
			if (isSendAlarm(alarmType, alarmMeta.getSendTimeSpan())) {
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
				Set<String> mobiles = new HashSet<String>();
				Set<String> emails = new HashSet<String>();
				if (alarmMeta.getIsSendSwallow()) {
					fillReciever(getSwallowIps(), mobiles, emails);
				}
				if (alarmMeta.getIsSendBusiness()) {
					fillReciever(getRelatedIps(), mobiles, emails);
				}
				sendAlarm(mobiles, emails, alarm, alarmMeta);
			}
		} else {
			logger.error("[sendMessage] cannot find related alarmMeta. metaId {}. ", alarmType.getNumber());
		}
	}

	protected boolean isAlarm(Map<String, Long> alarms, String key, int timeSpan) {
		long dValue = 0;
		if (alarms.containsKey(key)) {
			dValue = System.currentTimeMillis() - alarms.get(key).longValue();
			if (dValue > timeSpan * 60 * 1000) {
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

	protected long getNextSeq() {
		return seqGeneratorService.nextSeq(ALARMEVENTID_CATEGORY);
	}

	private Set<String> getSwallowIps() {
		List<String> serverIps = ipCollectorService.getProducerServerIps();
		if (serverIps != null && serverIps.size() > 0) {
			Set<String> ips = new HashSet<String>();
			ips.addAll(serverIps);
			return ips;
		}
		return null;
	}

	private void sendAlarm(Set<String> mobiles, Set<String> emails, Alarm alarm, AlarmMeta alarmMeta) {
		logger.info("[sendAlarm] eventId {}. ", alarm.getEventId());
		if (alarmMeta.getIsMailMode()) {
			alarmService.sendMail(emails, alarm);
		}
		if (alarmMeta.getIsSmsMode()) {
			alarmService.sendSms(mobiles, alarm);
		}
		if (alarmMeta.getIsWeiXinMode()) {
			alarmService.sendWeiXin(emails, alarm);
		}
		alarmService.insert(alarm);
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
