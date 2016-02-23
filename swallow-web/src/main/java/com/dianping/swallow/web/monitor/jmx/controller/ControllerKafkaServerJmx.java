package com.dianping.swallow.web.monitor.jmx.controller;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.ReportableKafka;
import com.dianping.swallow.web.monitor.jmx.broker.AbstractKafkaServerJmx;
import com.dianping.swallow.web.monitor.jmx.event.ControllerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/17  下午6:43.
 */
@Component
public class ControllerKafkaServerJmx extends AbstractKafkaServerJmx implements ReportableKafka {

    private void reportKafkaWrongEvent(ServerType serverType, List<String> liveControllerIps, List<String> cluster, int id) {

        id2States.put(id, Boolean.TRUE);
        ControllerKafkaEvent controllerKafkaEvent = (ControllerKafkaEvent) createEvent();
        controllerKafkaEvent.setServerType(serverType);
        controllerKafkaEvent.setLiveControllerIps(liveControllerIps);
        controllerKafkaEvent.setIp(StringUtils.join(cluster, KafkaEvent.DELIMITOR));
        report(controllerKafkaEvent);
    }

    private void reportKafkaOKEvent(List<String> kafkaIps, int id) {

        boolean wentWrong = id2States.get(id);
        if (wentWrong) { //恢复
            ControllerKafkaEvent controllerKafkaEvent = (ControllerKafkaEvent) createEvent();
            controllerKafkaEvent.setServerType(ServerType.CONTROLLER_STATE_OK);
            controllerKafkaEvent.setLiveControllerIps(kafkaIps);
            controllerKafkaEvent.setIp(StringUtils.join(kafkaIps, KafkaEvent.DELIMITOR));
            report(controllerKafkaEvent);
        }
        id2States.put(id, Boolean.FALSE);
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createControllerKafkaEvent();
    }

    @Override
    protected void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster, int id) {

        int liveSize = liveControllerIps.size();

        if (liveSize > 1) {
            reportKafkaWrongEvent(ServerType.CONTROLLER_MULTI_STATE, liveControllerIps, cluster, id);
        } else if (liveSize == 0) {
            reportKafkaWrongEvent(ServerType.CONTROLLER_STATE, liveControllerIps, cluster, id);
        } else {
            reportKafkaOKEvent(cluster, id);
        }

    }
}
