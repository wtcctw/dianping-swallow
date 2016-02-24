package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmType;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author   mingdongli
 * 16/2/18  上午10:02.
 */
public class ControllerKafkaEvent extends KafkaEvent{

    protected List<String> liveControllerIps;

    public List<String> getLiveControllerIps() {
        return liveControllerIps;
    }

    public void setLiveControllerIps(List<String> liveControllerIps) {
        this.liveControllerIps = liveControllerIps;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case CONTROLLER_STATE:
                sendMessage(AlarmType.SERVER_CONTROLLER_STATE);
                break;
            case CONTROLLER_MULTI_STATE:
                sendMessage(AlarmType.SERVER_CONTROLLER_MULTI_STATE);
                break;
            case CONTROLLER_STATE_OK:
                sendMessage(AlarmType.SERVER_CONTROLLER_STATE_OK);
                break;
            default:
                break;
        }
    }

    @Override
    protected List<String> displayIps() {

        if(!liveControllerIps.isEmpty()){
            return liveControllerIps;
        }
        String ip = getIp();
        if(StringUtils.isBlank(ip)){
            return Collections.emptyList();
        }
        return Arrays.asList(ip.split(KafkaEvent.DELIMITOR));
    }

}
