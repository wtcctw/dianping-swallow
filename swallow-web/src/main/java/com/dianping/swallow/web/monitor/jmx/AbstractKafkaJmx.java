package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.monitor.jmx.broker.BrokerStates;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.dianping.swallow.web.service.JmxResourceService;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author   mingdongli
 * 16/1/28  下午7:08.
 */
public abstract class AbstractKafkaJmx extends ConfigedKafkaJmx implements ReportableKafka {

    protected Set<Pair<String, Integer>> brokers = new HashSet<Pair<String, Integer>>();

    protected ScheduledExecutorService jmxFetcherExecutor = Executors.newScheduledThreadPool(1);

    private static final String CAT_TYPE = "Jmx-Fetcher";

    @Resource(name = "kafkaServerResourceService")
    protected KafkaServerResourceService kafkaServerResourceService;

    @Resource(name = "jmxResourceService")
    protected JmxResourceService jmxResourceService;

    @Override
    protected void doInitialize() throws Exception {

        initJmx();
        initBrokers();
        initMetricName2Clazz();
        initCustomConfig();

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
                brokers.add(new Pair<String, Integer>(ip, port));
            }
        }
    }

    protected void initCustomConfig(){
        //override by subclass
    }

    protected Map<Integer, List<String>> loadKafkaClusters(){

        Map<Integer, List<String>> groupId2KafkaCluster = new HashMap<Integer, List<String>>();
        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for(KafkaServerResource kafkaServerResource : kafkaServerResources){
            int groupId = kafkaServerResource.getGroupId();
            List<String> brokerIps = groupId2KafkaCluster.get(groupId);
            if(brokerIps == null){
                brokerIps = new ArrayList<String>();
            }
            brokerIps.add(kafkaServerResource.getIp());
            groupId2KafkaCluster.put(groupId, brokerIps);
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

        for (Pair<String, Integer> pair: brokers) {
            String ip = pair.getFirst();
            int port = pair.getSecond();
            for (Map.Entry<MetricName, Class<?>> entry : metricName2Clazz.entrySet()) {
                try {
                    Object value = getMBeanValue(ip, port, entry.getKey(), entry.getValue());
                    int state = Integer.parseInt(value == null ? "0" : value.toString());
                    brokerStatesMap.put(ip, BrokerStates.findByState(state));
                } catch (IOException e) {
                    logger.warn(String.format("Fetch MBean of name %s from %s failed", JMX_NAME, ip));
                    brokerStatesMap.put(ip, BrokerStates.NotRunning);
                }
            }
        }

        return brokerStatesMap;
    }

    @Override
    public boolean isReport(Event event){

        String ip = event.getRelated();
        if(StringUtils.isBlank(ip)){
            return true;
        }

        int index = ip.indexOf(KafkaEvent.DELIMITOR);
        if(index == -1){
            KafkaServerResource kafkaServerResource = kafkaServerResourceService.findByIp(ip);
            if(kafkaServerResource == null){
                return false;
            }
            boolean alarm = kafkaServerResource.isAlarm();
            if(!alarm){
                return alarm;
            }

            List<JmxResource> jmxResources = jmxResourceService.findByName(JMX_NAME);
            for(JmxResource jmxResource : jmxResources){
                List<IpInfo> ipInfos = jmxResource.getBrokerIpInfos();
                for(IpInfo ipInfo : ipInfos){
                    if(ip.equals(ipInfo.getIp())){
                        return ipInfo.isAlarm();
                    }
                }
            }
        }else {
            boolean alarm = false;  //集群
            String[] brokerIps = ip.split(KafkaEvent.DELIMITOR);
            for(String brokerIp : brokerIps){
                KafkaServerResource kafkaServerResource = kafkaServerResourceService.findByIp(brokerIp);
                if(kafkaServerResource == null){
                    continue;
                }
                alarm = kafkaServerResource.isAlarm() || alarm;
            }
            if(!alarm){
                return alarm;
            }

            alarm = !alarm;
            List<JmxResource> jmxResources = jmxResourceService.findByName(JMX_NAME);
            for(JmxResource jmxResource : jmxResources){
                List<IpInfo> ipInfos = jmxResource.getBrokerIpInfos();
                for(IpInfo ipInfo : ipInfos){
                    if(Arrays.asList(brokerIps).contains(ipInfo.getIp())){
                        alarm = alarm || ipInfo.isAlarm();
                    }
                }
                break;
            }
            return alarm;
        }

        return true;
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
