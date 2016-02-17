package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/2  下午3:34.
 */
public class BrokerKafkaEvent extends KafkaEvent {

    protected List<String> downBrokerIps;

    protected List<String> liveControllerIps;

    public List<String> getDownBrokerIps() {
        return downBrokerIps;
    }

    public void setDownBrokerIps(List<String> downBrokerIps) {
        this.downBrokerIps = downBrokerIps;
    }

    public List<String> getLiveControllerIps() {
        return liveControllerIps;
    }

    public void setLiveControllerIps(List<String> liveControllerIps) {
        this.liveControllerIps = liveControllerIps;
    }

    @Override
    public String getMessage(String template) {
        String message = template;
        if (StringUtils.isNotBlank(message)) {
            message = StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, getIp());
            message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
        }
        return message;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case BROKER_STATE:
                sendMessage(AlarmType.SERVER_BROKER_STATE);
                break;
            case BROKER_STATE_OK:
                sendMessage(AlarmType.SERVER_BROKER_STATE_OK);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
        String key = getIp() + KEY_SPLIT + alarmType.getNumber();
        return isAlarm(lastAlarms, key, alarmMeta);
    }

}
