package com.dianping.swallow.consumerserver.auth.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;

/**
 * 控制消费者是否允许消费
 */
public class ConsumerAuthControllerImpl implements ConsumerAuthController, ConfigChangeListener {

    private static final Logger logger        = LogManager.getLogger(ConsumerAuthControllerImpl.class);

    private static final String IP_SPLIT   = ";";

    private static final String LION_KEY   = "swallow.disable.ipList";

    private Set<String>         disableIps = new HashSet<String>();

    private DynamicConfig       lionDynamicConfig;

    public void init() {
        build();

        //监听lion
        lionDynamicConfig.addConfigChangeListener(this);

    }

    @Override
    public void onConfigChange(String key, String value) {
        logger.info("Invoke onConfigChange, key='" + key + "', value='" + value + "'");
        key = key.trim();
        if (key.equals(LION_KEY)) {
            try {
                build();
            } catch (RuntimeException e) {
                logger.error("Error initialize 'topic white list' from lion ", e);
            }
        }
    }

    private void build() {
        String value = lionDynamicConfig.get(LION_KEY);

        Set<String> _ips = new HashSet<String>();

        if (value != null && value.length() > 0) {
            value = value.trim();
            String[] ips = value.split(IP_SPLIT);
            for (String t : ips) {
                if (!"".equals(t.trim())) {
                    _ips.add(t);
                }
            }
        }

        this.disableIps = _ips;

        logger.info("Disable ip list is :" + disableIps);
    }

    public void setLionDynamicConfig(DynamicConfig lionDynamicConfig) {
        this.lionDynamicConfig = lionDynamicConfig;
    }

    public void addTopic(String topic) {
        this.disableIps.add(topic);
    }

    @Override
    public boolean isValid(ConsumerInfo consumerInfo, String ip) {
        // 目前api接口只判断ip；
        return ip != null && !disableIps.contains(ip);

    }

}
