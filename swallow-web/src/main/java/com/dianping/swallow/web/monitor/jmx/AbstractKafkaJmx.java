package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.monitor.jmx.broker.BrokerKafkajmx;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public abstract class AbstractKafkaJmx extends AbstractReportableKafkaJmx implements ReportableKafkajmx, InitializingBean {

    protected Map<String, String> defaultJmxConnectorProperties = new HashMap<String, String>();

    protected final Logger logger = LogManager.getLogger(getClass());

    protected Set<InetSocketAddress> brokers = new HashSet<InetSocketAddress>();

    protected Map<MetricName, Class<?>> metricName2Clazz = new HashMap<MetricName, Class<?>>();

    protected Map<BrokerKafkajmx.MetricKey, MetricName> type2MetricName = new HashMap<BrokerKafkajmx.MetricKey, MetricName>();

    protected ScheduledExecutorService jmxFetcherExecutor = Executors.newScheduledThreadPool(1);

    private static final String CAT_TYPE = "Jmx-Fetcher";

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @Override
    public void afterPropertiesSet() throws Exception {

        initJmxProperties();
        initBrokers();
        initMetricName2Clazz();

        jmxFetcherExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName());
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
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

    private void initBrokers() {

        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for (KafkaServerResource kafkaServerResource : kafkaServerResources) {
            String ip = kafkaServerResource.getIp();
            int port = kafkaServerResource.getPort();
            if (StringUtils.isNotBlank(ip) && port > 0) {
                brokers.add(new InetSocketAddress(ip, port));
            }
        }
    }

    private void initJmxProperties(){
        defaultJmxConnectorProperties.put("jmx.remote.x.request.waiting.timeout", "3000");
        defaultJmxConnectorProperties.put("jmx.remote.x.notification.fetch.timeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.connectionTimeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.handshakeTimeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.responseTimeout", "3000");
    }

    abstract protected void initMetricName2Clazz();

    abstract protected void doFetchJmxMetric();

}
