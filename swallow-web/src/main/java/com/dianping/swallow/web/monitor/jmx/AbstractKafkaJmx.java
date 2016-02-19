package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.monitor.jmx.broker.BrokerStates;
import com.dianping.swallow.web.service.JmxResourceService;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author   mingdongli
 * 16/1/28  下午7:08.
 */
public abstract class AbstractKafkaJmx extends AbstractReportableKafkaJmx implements ReportableKafkaJmx, InitializingBean {

    protected Set<InetSocketAddress> brokers = new HashSet<InetSocketAddress>();

    protected ScheduledExecutorService jmxFetcherExecutor = Executors.newScheduledThreadPool(1);

    private static final String CAT_TYPE = "Jmx-Fetcher";

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @Resource(name = "jmxResourceService")
    protected JmxResourceService jmxResourceService;

    @Override
    public void afterPropertiesSet() throws Exception {

        initJmx();
        initBrokers();
        initMetricName2Clazz();

        jmxFetcherExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName());
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
                        initBrokers();
                        doFetchJmxMetric();
                    }
                });
            }
        }, getDelay(), getInterval(), TimeUnit.SECONDS);

    }

    @Override
    public Object getMBeanValue(String host, int port, MetricName metricName, Class<?> clazz) throws IOException {

        String urlString = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(host).append(":").append(port).append("/jmxrmi").toString();
        final JMXServiceURL jmxServiceUrl = new JMXServiceURL(urlString);
        final JMXConnector jMXConnector = JMXConnectorFactory.connect(jmxServiceUrl, defaultJmxConnectorProperties);
        final MBeanServerConnection mBeanServerConnection = jMXConnector.getMBeanServerConnection();
        try {
            String mBeanName = metricName.getMBeanName().replaceAll("\"", "");

            if (clazz.isAssignableFrom(JmxReporter.GaugeMBean.class)) {
                JmxReporter.GaugeMBean mbean = MBeanServerInvocationHandler.newProxyInstance(mBeanServerConnection, new ObjectName(mBeanName), JmxReporter.GaugeMBean.class, true);
                return mbean.getValue();
            } else if (clazz.isAssignableFrom(JmxReporter.MeterMBean.class)) {
                JmxReporter.MeterMBean mbean = MBeanServerInvocationHandler.newProxyInstance(mBeanServerConnection, new ObjectName(mBeanName), JmxReporter.MeterMBean.class, true);
                return mbean;
            } else {
                throw new UnsupportedClassVersionError(String.format("Class %s is not supported", clazz.getSimpleName()));
            }

        } catch (MalformedObjectNameException e) {
            logger.warn(String.format("Generate ObjectName for type %s and name %s failed", metricName.getType(), metricName.getName()));
        } finally {
            jMXConnector.close();
        }
        return null;
    }

    protected void initBrokers() {

        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for (KafkaServerResource kafkaServerResource : kafkaServerResources) {
            String ip = kafkaServerResource.getIp();
            int port = kafkaServerResource.getPort();
            if (StringUtils.isNotBlank(ip) && port > 0) {
                brokers.add(new InetSocketAddress(ip, port));
            }
        }
    }

    protected Map<Integer, List<String>> loadKafkaClusters(){

        Map<Integer, List<String>> groupId2KafkaCluster = new HashMap<Integer, List<String>>();
        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for(KafkaServerResource kafkaServerResource : kafkaServerResources){
            int groupId = kafkaServerResource.getGroupId();
            List<String> brokerips = groupId2KafkaCluster.get(groupId);
            if(brokerips == null){
                brokerips = new ArrayList<String>();
            }
            brokerips.add(kafkaServerResource.getIp());
            groupId2KafkaCluster.put(groupId, brokerips);
        }
        return groupId2KafkaCluster;

    }

    private void initMetricName2Clazz() {
        List<JmxResource> jmxResources = jmxResourceService.findByName(JMX_NAME);
        for (JmxResource jmxResource : jmxResources) {
            MetricName metricName = new MetricName(jmxResource.getGroup(), jmxResource.getType(), JMX_NAME);
            type2MetricName.put(new MetricKey(jmxResource.getType(), JMX_NAME), metricName);
            metricName2Clazz.put(metricName, getMetricClazz(jmxResource.getClazz()));
        }
    }

    protected Map<String, BrokerStates> fetchMBeanBrokerStates() {

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

    abstract protected void doFetchJmxMetric();

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
