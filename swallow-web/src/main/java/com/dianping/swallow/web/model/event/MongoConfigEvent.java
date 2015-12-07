package com.dianping.swallow.web.model.event;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoConfigEvent extends ServerEvent {

    private static final Map<String, AlarmRecord> lastAlarms = new ConcurrentHashMap<String, AlarmRecord>();

    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public MongoConfigEvent setTopicName(String topicName) {
        this.topicName = topicName;
        return this;
    }

    @Override
    public String getMessage(String template) {
        String message = template;
        if (StringUtils.isNotBlank(message)) {
            message = StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, getIp());
            message = StringUtils.replace(message, AlarmMeta.TOPIC_TEMPLATE, getTopicName());
            message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
        }
        return message;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case MONGO_CONFIG:
                sendMessage(AlarmType.SERVER_MONGO_CONFIG);
                break;
            case MONGO_CONFIG_OK:
                sendMessage(AlarmType.SERVER_MONGO_CONFIG_OK);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
        String key = getIp() + KEY_SPLIT + topicName + alarmType.getNumber();
        return isAlarm(lastAlarms, key, alarmMeta);
    }

    @Override
    public String toString() {
        return "MongoConfigEvent [topicName=" + topicName + super.toString() + "]";
    }

}
