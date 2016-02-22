package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/22  下午4:50.
 */
public class ControllerElectionEvent extends KafkaEvent {

    private List<String> cluster;

    private String currentControllerIp;

    private String previousControllerIp;

    public List<String> getCluster() {
        return cluster;
    }

    public void setCluster(List<String> cluster) {
        this.cluster = cluster;
    }

    public String getCurrentControllerIp() {
        return currentControllerIp;
    }

    public void setCurrentControllerIp(String currentControllerIp) {
        this.currentControllerIp = currentControllerIp;
    }

    public String getPreviousControllerIp() {
        return previousControllerIp;
    }

    public void setPreviousControllerIp(String previousControllerIp) {
        this.previousControllerIp = previousControllerIp;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case CONTROLLER_ELECTION_STATE:
                sendMessage(AlarmType.SERVER_CONTROLLER_ELECTION_STATE);
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
            message = StringUtils.replace(message, AlarmMeta.CURRENTVALUE_TEMPLATE, getCurrentControllerIp());
            message = StringUtils.replace(message, AlarmMeta.EXPECTEDVALUE_TEMPLATE, getPreviousControllerIp());
        }
        return message;
    }

    @Override
    protected List<String> displayIps() {
        return getCluster();
    }
}
