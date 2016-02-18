package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.model.event.AlarmRecord;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.util.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author   mingdongli
 * 16/2/2  下午4:46.
 */
public abstract class KafkaEvent extends ServerEvent {

    public static final String DELIMITOR = ",";

    protected static final Map<String, AlarmRecord> lastAlarms = new ConcurrentHashMap<String, AlarmRecord>();

    protected String prettyDisplayList(List<String> list){
        if(list == null){
            return StringUtils.EMPTY;
        }
        return StringUtils.join(list, DELIMITOR);
    }

    @Override
    public RelatedType getRelatedType() {
        return RelatedType.K_SERVER_IP;
    }

    @Override
    public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
        String key = StringUtils.join(displayIps(), DELIMITOR) + KEY_SPLIT + alarmType.getNumber();
        return isAlarm(lastAlarms, key, alarmMeta);
    }

    @Override
    public String getMessage(String template) {
        String message = template;
        if (org.codehaus.plexus.util.StringUtils.isNotBlank(message)) {
            message = org.codehaus.plexus.util.StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, prettyDisplayList(displayIps()));
            message = org.codehaus.plexus.util.StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
        }
        return message;
    }

    protected abstract List<String> displayIps();

}
