package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.web.monitor.jmx.broker.BrokerStates;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;

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
public abstract class AbstractConfigKafkaJmx extends AbstractKafkaJmx{

    private static final String BROKER_GROUP = "kafka.server";

    protected boolean wentWrong = false;

    //保存到数据库，写死了
    @Override
    protected void initMetricName2Clazz() {

        MetricName metricName = new MetricName(BROKER_GROUP, "KafkaServer", "BrokerState");
        type2MetricName.put(new MetricKey("KafkaServer", "BrokerState"), metricName);
        metricName2Clazz.put(metricName, JmxReporter.GaugeMBean.class);
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
//        List<String> downBrokerIps = new ArrayList<String>();
//        List<String> liveControllerIps = new ArrayList<String>();
//        for (Map.Entry<String, BrokerStates> entry : brokerStatesMap.entrySet()) {
//            BrokerStates state = entry.getValue();
//
//            if (state == BrokerStates.RunningAsBroker) {
//                //nothing to do;
//            } else if (state == BrokerStates.RunningAsController) {
//                liveControllerIps.add(entry.getKey());
//            } else {
//                downBrokerIps.add(entry.getKey());
//            }
//        }
//
//        checkKafkaStates(downBrokerIps, liveControllerIps);

//        int liveSize = liveControllerIps.size();
//        int downSize = downBrokerIps.size();
//
//        if (downSize > 0 || liveSize > 1 || liveSize == 0) {
//            reportKafkaWrongEvent(downBrokerIps, liveControllerIps);
//        }else {
//            if(wentWrong){ //恢复
//                reportKafkaOKEvent(downBrokerIps, liveControllerIps);
//            }
//        }

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
        return 10; //metaData延迟5s中
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
