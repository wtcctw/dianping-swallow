package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.monitor.jmx.broker.BrokerStates;
import com.yammer.metrics.core.MetricName;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/17  下午6:23.
 */
public abstract class AbstractKafkaServerJmx extends AbstractKafkaJmx{

    private static final String BROKER_GROUP = "kafka.server";

    protected boolean wentWrong = false;

    @Override
    protected void initMetricName2Clazz() {

        jmxGroup = BROKER_GROUP;
        List<JmxResource> jmxResources = jmxResourceService.findByGroup(jmxGroup);
        for(JmxResource jmxResource : jmxResources){
            MetricName metricName = new MetricName(jmxGroup, jmxResource.getType(), jmxResource.getName());
            type2MetricName.put(new MetricKey(jmxResource.getType(), jmxResource.getName()), metricName);
            metricName2Clazz.put(metricName, getMetricClazz(jmxResource.getClazz()));
        }
    }

    @Override
    protected void doFetchJmxMetric() {

        initBrokers();
        Map<String, BrokerStates> brokerStatesMap = fetchBrokerStates();
        Map<Integer, List<String>> groupId2KafkaCluster = loadKafkaClusters();
        for(List<String> cluster : groupId2KafkaCluster.values()){
            List<String> downBrokerIps = new ArrayList<String>();
            List<String> liveControllerIps = new ArrayList<String>();
            for(String kafkaIp : cluster){
                BrokerStates state = brokerStatesMap.get(kafkaIp);

                if (state == BrokerStates.RunningAsBroker) {
                    //nothing to do;
                } else if (state == BrokerStates.RunningAsController) {
                    liveControllerIps.add(kafkaIp);
                } else {
                    downBrokerIps.add(kafkaIp);
                }
            }
            checkKafkaStates(downBrokerIps, liveControllerIps, cluster);
        }

    }

    private Map<String, BrokerStates> fetchBrokerStates() {

        Map<String, BrokerStates> brokerStatesMap = new HashMap<String, BrokerStates>();

        for (InetSocketAddress inetSocketAddress : brokers) {
            String hostName = inetSocketAddress.getHostName();
            int port = inetSocketAddress.getPort();
            for (Map.Entry<MetricName, Class<?>> entry : metricName2Clazz.entrySet()) {
                try {
                    Object value = getMBeanValue(hostName, port, entry.getKey(), entry.getValue());
                    int state = Integer.parseInt(value == null ? "0" : value.toString());
                    brokerStatesMap.put(hostName, BrokerStates.findByState(state));
                } catch (IOException e) {
                    logger.warn(String.format("Fetch MBean of %s failed", entry.getValue().toString()));
                    brokerStatesMap.put(hostName, BrokerStates.NotRunning);
                }
            }
        }

        return brokerStatesMap;
    }

    @Override
    protected int getInterval() {
        return 30;
    }

    @Override
    protected int getDelay() {
        return 10;
    }

    protected abstract void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster);

    public static class MetricKey {
        private String type;

        private String name;

        public MetricKey(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MetricKey metricKey = (MetricKey) o;

            if (type != null ? !type.equals(metricKey.type) : metricKey.type != null) return false;
            return !(name != null ? !name.equals(metricKey.name) : metricKey.name != null);

        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
