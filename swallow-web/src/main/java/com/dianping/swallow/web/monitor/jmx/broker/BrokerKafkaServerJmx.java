package com.dianping.swallow.web.monitor.jmx.broker;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.ReportableKafka;
import com.dianping.swallow.web.monitor.jmx.event.BrokerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.dianping.swallow.web.monitor.jmx.listener.KafkaEventListener;
import com.dianping.swallow.web.monitor.zookeeper.topic.TopicCurator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author   mingdongli
 * 16/1/29  下午2:20.
 */
@Component
public class BrokerKafkaServerJmx extends AbstractKafkaServerJmx implements ReportableKafka {

    @Autowired
    private TopicCurator topicCurator;

    private List<String> downBroker = new ArrayList<String>();

    private List<KafkaEventListener> listeners = new ArrayList<KafkaEventListener>();

    private void reportKafkaWrongEvent(List<String> downBrokerIps, List<String> cluster, int id) {

        id2States.put(id, Boolean.TRUE);
        downBroker = Collections.unmodifiableList(downBrokerIps);
        BrokerKafkaEvent brokerKafkaEvent = (BrokerKafkaEvent) createEvent();
        brokerKafkaEvent.setDownBrokerIps(downBrokerIps);
        brokerKafkaEvent.setIp(StringUtils.join(cluster, KafkaEvent.DELIMITOR));
        brokerKafkaEvent.setServerType(ServerType.BROKER_STATE);
        report(brokerKafkaEvent);
        for(KafkaEventListener listener : listeners){
            listener.onKafkaEvent(brokerKafkaEvent);
        }
    }

    private void reportKafkaOKEvent(List<String> kafkaIps, int id) {

        boolean wentWrong = id2States.get(id);
        if (wentWrong) { //恢复
            BrokerKafkaEvent brokerKafkaEvent = (BrokerKafkaEvent) createEvent();
            brokerKafkaEvent.setServerType(ServerType.BROKER_STATE_OK);
            brokerKafkaEvent.setDownBrokerIps(Collections.EMPTY_LIST);
            brokerKafkaEvent.setIp(StringUtils.join(downBroker, KafkaEvent.DELIMITOR));
            report(brokerKafkaEvent);
            for(KafkaEventListener listener : listeners){
                listener.onKafkaEvent(brokerKafkaEvent);
            }
        }
        id2States.put(id, Boolean.FALSE);
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createBrokerKafkaEvent();
    }

    @Override
    protected void initCustomConfig(){
        super.initCustomConfig();
        listeners.add(topicCurator);
    }

    @Override
    protected void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster, int id) {

        int downSize = downBrokerIps.size();

        if (downSize > 0) {
            reportKafkaWrongEvent(downBrokerIps, cluster, id);
        } else {
            reportKafkaOKEvent(cluster, id);
        }
    }
}
