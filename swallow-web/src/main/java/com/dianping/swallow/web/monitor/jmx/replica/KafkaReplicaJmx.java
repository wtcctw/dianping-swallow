package com.dianping.swallow.web.monitor.jmx.replica;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.AbstractKafkaJmx;
import com.dianping.swallow.web.monitor.jmx.JmxConfig;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.UnderReplicaEvent;
import com.yammer.metrics.core.MetricName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/18  下午1:18.
 */
@Component
public class KafkaReplicaJmx extends AbstractKafkaJmx {

    //表明是否出错
    private Map<String, Boolean> ip2States = new HashMap<String, Boolean>();

    @Value("${swallow.web.monitor.jmx.underreplica.threshold}")
    private int THRESHHOLD = 0;

    @Override
    protected void doFetchJmxMetric() {

        Map<String, Integer> underReplicaMap = getUnderReplicaMap();
        for (Map.Entry<String, Integer> entry : underReplicaMap.entrySet()) {
            int size = entry.getValue();

            if (size > THRESHHOLD) {
                reportUnderReplicaWrongEvent(entry.getKey(), size, THRESHHOLD);
            } else {
                reportUnderReplicaOKEvent(entry.getKey());
            }
        }
    }

    private void reportUnderReplicaWrongEvent(String brokerIp, int currentSize, int expectedSize) {

        ip2States.put(brokerIp, Boolean.TRUE);
        UnderReplicaEvent underReplicaEvent = (UnderReplicaEvent) createEvent();
        underReplicaEvent.setIp(brokerIp);
        underReplicaEvent.setCurrentUnderReplicaSize(currentSize);
        underReplicaEvent.setExpectedUnderReplicaSize(expectedSize);
        underReplicaEvent.setServerType(ServerType.UNDERREPLICA_STATE);
        report(underReplicaEvent);
    }

    private void reportUnderReplicaOKEvent(String brokerIp) {

        boolean wentWrong = ip2States.get(brokerIp);
        if (wentWrong) {
            UnderReplicaEvent underReplicaEvent = (UnderReplicaEvent) createEvent();
            underReplicaEvent.setIp(brokerIp);
            underReplicaEvent.setServerType(ServerType.UNDERREPLICA_STATE_OK);
            report(underReplicaEvent);
        }
        ip2States.put(brokerIp, Boolean.FALSE);
    }

    private Map<String, Integer> getUnderReplicaMap() {

        Map<String, Integer> underReplicaMap = new HashMap<String, Integer>();

        for (Pair<String, Integer> pair : brokers) {
            String ip = pair.getFirst();
            int port = pair.getSecond();
            for (Map.Entry<MetricName, Class<?>> entry : metricName2Clazz.entrySet()) {
                try {
                    Object value = getMBeanValue(ip, port, entry.getKey(), entry.getValue());
                    int size = Integer.parseInt(value == null ? "0" : value.toString());
                    underReplicaMap.put(ip, size);
                } catch (IOException e) {
                    logger.warn(String.format("Fetch MBean of %s from %s failed", JMX_CONFIG.getName(), ip));
                    underReplicaMap.put(ip, 0);
                }
            }
        }

        return underReplicaMap;
    }

    @Override
    protected void initCustomConfig() {

        for (Pair<String, Integer> pair : brokers) {
            ip2States.put(pair.getFirst(), Boolean.FALSE);
        }
    }

    @Override
    public boolean isReport(Event event){
        return false;
    }

    @Override
    protected int getInterval() {
        return 30;
    }

    @Override
    protected int getDelay() {
        return 9;
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createUnderReplicaEvent();
    }

    @Override
    protected JmxConfig getJmxConfig(){

        JmxConfig jmxConfig = new JmxConfig();
        jmxConfig.setGroup("kafka.server");
        jmxConfig.setName("UnderReplicatedPartitions");
        jmxConfig.setType("ReplicaManager");
        jmxConfig.setClazz("Gauge");
        return jmxConfig;
    }

}
