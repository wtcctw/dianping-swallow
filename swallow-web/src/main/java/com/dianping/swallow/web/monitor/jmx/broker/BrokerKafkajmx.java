package com.dianping.swallow.web.monitor.jmx.broker;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.monitor.jmx.AbstractKafkaJmx;
import com.dianping.swallow.web.monitor.jmx.ReportableKafkajmx;
import com.dianping.swallow.web.monitor.jmx.event.BrokerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/1/29  下午2:20.
 */
@Component
public class BrokerKafkajmx extends AbstractKafkaJmx implements ReportableKafkajmx {

    private static final String BROKER_GROUP = "kafka.server";

    private boolean wentWrong = false;

    //保存到数据库，写死了
    @Override
    protected void initMetricName2Clazz() {

        MetricName metricName = new MetricName(BROKER_GROUP, "KafkaServer", "BrokerState");
        type2MetricName.put(new MetricKey("KafkaServer", "BrokerState"), metricName);
        metricName2Clazz.put(metricName, JmxReporter.GaugeMBean.class);
    }

    @Override
    protected void doFetchJmxMetric() {

        //需要检查数量？
        Map<String, BrokerStates> brokerStatesMap = fetchBrokerStates();
        List<String> downBrokerIps = new ArrayList<String>();
        List<String> liveControllerIps = new ArrayList<String>();
        for (Map.Entry<String, BrokerStates> entry : brokerStatesMap.entrySet()) {
            BrokerStates state = entry.getValue();

            if (state == BrokerStates.RunningAsBroker) {
                //nothing to do;
            } else if (state == BrokerStates.RunningAsController) {
                liveControllerIps.add(entry.getKey());
            } else {
                downBrokerIps.add(entry.getKey());
            }
        }

        if (downBrokerIps.size() > 0 || liveControllerIps.size() > 1) {
            BrokerKafkaEvent brokerKafkaEvent = (BrokerKafkaEvent) createEvent();
            brokerKafkaEvent.setDownBrokerIps(downBrokerIps);
            brokerKafkaEvent.setLiveControllerIps(liveControllerIps);
            brokerKafkaEvent.setServerType(ServerType.BROKER_STATE);
            if (!wentWrong) {
                report(brokerKafkaEvent);
                wentWrong = true;
            }
        }else {
            if(wentWrong){
                BrokerKafkaEvent brokerKafkaEvent = (BrokerKafkaEvent) createEvent();
                brokerKafkaEvent.setDownBrokerIps(downBrokerIps);
                brokerKafkaEvent.setLiveControllerIps(liveControllerIps);
                brokerKafkaEvent.setServerType(ServerType.BROKER_STATE_OK);
                report(brokerKafkaEvent);
            }
            wentWrong = false;
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
                }
            }
        }

        return brokerStatesMap;
    }

    @Override
    protected int getInterval() {
        return 60 * 60;
    }

    @Override
    protected int getDelay() {
        return 0;
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createBrokerKafkaEvent();
    }

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
