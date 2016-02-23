package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.web.monitor.jmx.broker.AbstractKafkaServerJmx;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.JmxReporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/18  下午10:07.
 */
public abstract class ConfigedKafkaJmx extends AbstractReportableKafka{

    private Map<String, Class> type2Clazz = new HashMap<String, Class>();

    protected Map<String, String> defaultJmxConnectorProperties = new HashMap<String, String>();

    protected Map<MetricName, Class<?>> metricName2Clazz = new HashMap<MetricName, Class<?>>();

    protected Map<AbstractKafkaServerJmx.MetricKey, MetricName> type2MetricName = new HashMap<AbstractKafkaServerJmx.MetricKey, MetricName>();

    protected String JMX_NAME;

    private void initJmxProperties(){
        defaultJmxConnectorProperties.put("jmx.remote.x.request.waiting.timeout", "3000");
        defaultJmxConnectorProperties.put("jmx.remote.x.notification.fetch.timeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.connectionTimeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.handshakeTimeout", "3000");
        defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.responseTimeout", "3000");
    }

    private void initJmxClazz(){
        type2Clazz.put("Gauge", JmxReporter.GaugeMBean.class);
        type2Clazz.put("Meter", JmxReporter.MeterMBean.class);
        type2Clazz.put("Counter", JmxReporter.CounterMBean.class);
    }

    private void initJxmName(){
        JMX_NAME = getJmxName();
    }

    protected void initJmx(){
        initJmxProperties();
        initJmxClazz();
        initJxmName();
    }

    protected Class getMetricClazz(String type){
        Class metric = type2Clazz.get(type);
        if(metric == null){
            throw new UnsupportedClassVersionError(String.format("%s is not support", type));
        }
        return metric;
    }

    protected abstract String getJmxName();
}
