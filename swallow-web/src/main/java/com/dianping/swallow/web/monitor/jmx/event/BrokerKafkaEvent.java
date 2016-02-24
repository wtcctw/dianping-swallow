package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmType;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author   mingdongli
 * 16/2/2  下午3:34.
 */
public class BrokerKafkaEvent extends KafkaEvent {

    protected List<String> downBrokerIps;

    public List<String> getDownBrokerIps() {
        return downBrokerIps;
    }

    public void setDownBrokerIps(List<String> downBrokerIps) {
        this.downBrokerIps = downBrokerIps;
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
    protected List<String> displayIps() {
        if(!downBrokerIps.isEmpty()){
            return downBrokerIps;
        }
        String ip = getIp();
        if(StringUtils.isBlank(ip)){
            return Collections.emptyList();
        }
        return Arrays.asList(ip.split(KafkaEvent.DELIMITOR));
    }

}
