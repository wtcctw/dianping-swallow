package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.util.DateUtil;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Author   mingdongli
 * 16/2/19  上午10:21.
 */
public class UnderReplicaEvent extends KafkaEvent{

    private int currentUnderReplicaSize;

    private int expectedUnderReplicaSize;

    public int getCurrentUnderReplicaSize() {
        return currentUnderReplicaSize;
    }

    public void setCurrentUnderReplicaSize(int currentUnderReplicaSize) {
        this.currentUnderReplicaSize = currentUnderReplicaSize;
    }

    public int getExpectedUnderReplicaSize() {
        return expectedUnderReplicaSize;
    }

    public void setExpectedUnderReplicaSize(int expectedUnderReplicaSize) {
        this.expectedUnderReplicaSize = expectedUnderReplicaSize;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case UNDERREPLICA_STATE:
                sendMessage(AlarmType.SERVER_UNDERREPLICA_STATE);
                break;
            case UNDERREPLICA_STATE_OK:
                sendMessage(AlarmType.SERVER_UNDERREPLICA_STATE_OK);
                break;
            default:
                break;
        }
    }

    @Override
    public String getMessage(String template) {
        String message = template;
        if (org.codehaus.plexus.util.StringUtils.isNotBlank(message)) {
            message = StringUtils.replace(message, AlarmMeta.IP_TEMPLATE, prettyDisplayList(displayIps()));
            message = StringUtils.replace(message, AlarmMeta.CURRENTVALUE_TEMPLATE, Long.toString(getCurrentUnderReplicaSize()));
            message = StringUtils.replace(message, AlarmMeta.EXPECTEDVALUE_TEMPLATE, Long.toString(getExpectedUnderReplicaSize()));
            message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
        }
        return message;
    }

    @Override
    protected List<String> displayIps() {

        return Collections.singletonList(getIp());
    }
}
