package com.dianping.swallow.web.monitor.jmx.broker;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.AbstractConfigKafkaJmx;
import com.dianping.swallow.web.monitor.jmx.ReportableKafkaJmx;
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
public class ControllerKafkaJmx extends AbstractConfigKafkaJmx implements ReportableKafkaJmx {

    private void reportKafkaWrongEvent(ServerType serverType, List<String> liveControllerIps, List<String> cluster) {

        ControllerKafkaEvent controllerKafkaEvent = (ControllerKafkaEvent) createEvent();
        controllerKafkaEvent.setServerType(serverType);
        controllerKafkaEvent.setLiveControllerIps(liveControllerIps);
        controllerKafkaEvent.setIp(StringUtils.join(cluster, KafkaEvent.DELIMITOR));
        report(controllerKafkaEvent);
        wentWrong = true;
    }

    private void reportKafkaOKEvent(List<String> kafkaIps) {

        if (wentWrong) { //恢复
            ControllerKafkaEvent controllerKafkaEvent = (ControllerKafkaEvent) createEvent();
            controllerKafkaEvent.setServerType(ServerType.CONTROLLER_STATE_OK);
            controllerKafkaEvent.setLiveControllerIps(kafkaIps);
            controllerKafkaEvent.setIp(StringUtils.join(kafkaIps, KafkaEvent.DELIMITOR));
            report(controllerKafkaEvent);
            wentWrong = false;
        }
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createControllerKafkaEvent();
    }

    @Override
    protected void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster) {

        int liveSize = liveControllerIps.size();

        if (liveSize > 1) {
            reportKafkaWrongEvent(ServerType.CONTROLLER_MULTI_STATE, liveControllerIps, cluster);
        } else if (liveSize == 0) {
            reportKafkaWrongEvent(ServerType.CONTROLLER_STATE, liveControllerIps, cluster);
        } else {
            reportKafkaOKEvent(cluster);
        }

    }
}
