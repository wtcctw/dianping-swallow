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

    private long checkInterval = 10L;

    private long avgCountThreshold = 30L;

    private long qpsThreshold = 1L;

    private long noHasDataCount = 5L;

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

    public long getAvgCountThreshold() {
        return avgCountThreshold;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public long getQpsThreshold() {
        return qpsThreshold;
    }

    public long getNoHasDataCount() {
        return noHasDataCount;
    }
}
