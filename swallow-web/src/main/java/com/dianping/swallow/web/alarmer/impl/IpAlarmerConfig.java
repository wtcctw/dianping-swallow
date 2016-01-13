package com.dianping.swallow.web.alarmer.impl;

import com.dianping.swallow.common.internal.config.AbstractConfig;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author qi.yin
 *         2016/01/12  上午10:39.
 */
@Component
public class IpAlarmerConfig extends AbstractConfig implements AlarmerLifecycle {

    private static final String IP_ALARMER_FILE = "ip-alarmer.properties";

    private long clusterCheckInterval = 10L;

    private long unClusterCheckInterval = 20L;

    private long clusterNoHasDataCount = 5L;

    private long unClusterNoHasDataCount = 15L;

    private long avgCountThreshold = 30L;

    private long qpsThreshold = 1L;

    public IpAlarmerConfig() {
        super(IP_ALARMER_FILE);
    }

    @PostConstruct
    public void init() {
        try {
            loadConfig();
        } catch (Exception e) {
            logger.error("loadConfig error.", e);
        }
    }

    public long getUnClusterNoHasDataCount() {
        return unClusterNoHasDataCount;
    }

    public long getClusterCheckInterval() {
        return clusterCheckInterval;
    }

    public long getUnClusterCheckInterval() {
        return unClusterCheckInterval;
    }

    public long getClusterNoHasDataCount() {
        return clusterNoHasDataCount;
    }

    public long getAvgCountThreshold() {
        return avgCountThreshold;
    }

    public long getQpsThreshold() {
        return qpsThreshold;
    }

}
