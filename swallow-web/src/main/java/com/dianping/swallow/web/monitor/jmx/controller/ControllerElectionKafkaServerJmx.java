package com.dianping.swallow.web.monitor.jmx.controller;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.ReportableKafkaJmx;
import com.dianping.swallow.web.monitor.jmx.broker.AbstractKafkaServerJmx;
import com.dianping.swallow.web.monitor.jmx.event.ControllerElectionEvent;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/22  下午4:39.
 */
@Component
public class ControllerElectionKafkaServerJmx extends AbstractKafkaServerJmx implements ReportableKafkaJmx {

    protected Map<Integer, String> id2ControllerIp = new HashMap<Integer, String>();

    @Override
    protected void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster, int id) {

        int liveSize = liveControllerIps.size();

        if (liveSize == 1) {
            String controllerIp = id2ControllerIp.get(id);
            if(StringUtils.isBlank(controllerIp)){
                id2ControllerIp.put(id, liveControllerIps.get(0));
            }else{
                String currentControllerIp = liveControllerIps.get(0);
                if(!currentControllerIp.equals(controllerIp)){  //重新选举
                    reportKafkaWrongEvent(liveControllerIps, cluster, id);
                    id2ControllerIp.put(id, liveControllerIps.get(0));
                }
            }
        }
    }

    private void reportKafkaWrongEvent(List<String> liveControllerIps, List<String> cluster, int id) {

        ControllerElectionEvent controllerElectionEvent = (ControllerElectionEvent) createEvent();
        controllerElectionEvent.setServerType(ServerType.CONTROLLER_ELECTION_STATE);
        controllerElectionEvent.setPreviousControllerIp(id2ControllerIp.get(id));
        controllerElectionEvent.setCurrentControllerIp(liveControllerIps.get(0));
        controllerElectionEvent.setCluster(cluster);
        controllerElectionEvent.setIp(StringUtils.join(cluster, KafkaEvent.DELIMITOR));
        report(controllerElectionEvent);
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createControllerElectionEvent();
    }
}
