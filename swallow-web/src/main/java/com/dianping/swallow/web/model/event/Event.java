package com.dianping.swallow.web.model.event;

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
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 上午11:12:56
 */
public abstract class Event {

    protected static Logger logger = LogManager.getLogger(Event.class);

    protected static final String KEY_SPLIT = "&";

    // 1 minute
    private static final long timeUnit = 60 * 1000;

    private EventConfig eventConfig;

    private AlarmService alarmService;

    protected AlarmReceiverManager receiverManager;

    private AlarmMetaContainer alarmMetaContainer;

    // unit millis
    protected long checkInterval = 30 * 1000;

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

    public void setEventConfig(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public String getCheckIntervalBySecends() {
        return Long.toString(checkInterval / 1000);
    }

    public Event setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
        return this;
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
                AlarmReceiver alarmReceiver = fillReciever(alarmMeta, alarm);
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

            if (0 < dCheckValue && dCheckValue < 2 * checkInterval) {
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

    private AlarmReceiver fillReciever(AlarmMeta alarmMeta, Alarm alarm) {
        AlarmReceiver receiver = null;
        if (EnvUtil.isDev()) {
            if (eventConfig.getDevMobiles() != null && eventConfig.getDevEmails() != null) {
                receiver = new AlarmReceiver(eventConfig.getDevEmails(), eventConfig.getDevMobiles());
            }
            return receiver;
        } else {
            if (alarmMeta.getIsSendSwallow()) {
                receiver = receiverManager.getSwallowReceiver();
            } else {
                if ((alarm.getRelatedType().isPTopic() || alarm.getRelatedType().isCTopic() || alarm.getRelatedType().isCConsumerId()) &&
                        alarmMeta.getMajorTopics() != null && alarmMeta.getMajorTopics().contains(alarm.getRelated())) {
                    receiver = receiverManager.getSwallowReceiver();
                }
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

}
