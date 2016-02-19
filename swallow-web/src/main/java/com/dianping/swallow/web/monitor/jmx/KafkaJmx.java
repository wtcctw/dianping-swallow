package com.dianping.swallow.web.monitor.jmx;

import com.yammer.metrics.core.MetricName;

import java.io.IOException;

/**
 * Author   mingdongli
 * 16/1/28  下午7:03.
 */
public interface KafkaJmx {

    Object getMBeanValue(String host, int port, MetricName metricName, Class<?> clazz) throws IOException;
}
