package com.dianping.swallow.web.controller.filter.config;


import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;

/**
 * @author mingdongli
 *         15/10/26 上午11:14
 */
@Component
public class LionConfigManager implements ConfigChangeListener {

    private static final String CONSUMERSERVERURI_LENGTH = "swallow.web.consumer.consumerServerURI.length";

    private static final String WHITELIST_LENGTH = "swallow.web.topic.whitelist.length";

    @Autowired
    private LionDynamicConfig lionDynamicConfig;

    private int consumerServerUriLength = 0;

    private int whitelistLength = 0;

    private final Logger logger = LogManager.getLogger(getClass());

    @PostConstruct
    void initLionConfig() {

        String consumerServer = lionDynamicConfig.get(CONSUMERSERVERURI_LENGTH);
        consumerServerUriLength = StringToInt(consumerServer, consumerServerUriLength);

        String whitelist = lionDynamicConfig.get(WHITELIST_LENGTH);
        whitelistLength = StringToInt(whitelist, whitelistLength);

        lionDynamicConfig.addConfigChangeListener(this);

        if (logger.isInfoEnabled()) {
            logger.info(String.format("[Init consumerServerUriLength=%d and whitelistLength=%d]",
                    consumerServerUriLength, whitelistLength));
        }
    }

    public int getConsumerServerUriLength() {
        return consumerServerUriLength;
    }

    public int getWhitelistLength() {
        return whitelistLength;
    }

    private int StringToInt(String value, int defaultValue) {

        if (StringUtils.isNotBlank(value)) {
            return Integer.parseInt(value);
        }

        return defaultValue;
    }

    @Override
    public void onConfigChange(String key, String value) {

        if (key != null && key.equals(CONSUMERSERVERURI_LENGTH)) {
            if (logger.isInfoEnabled()) {
                logger.info("[onChange][" + CONSUMERSERVERURI_LENGTH + "]" + value);
            }
            consumerServerUriLength = StringToInt(value.trim(), consumerServerUriLength);
        } else if (key != null && key.equals(WHITELIST_LENGTH)) {
            if (logger.isInfoEnabled()) {
                logger.info("[onChange][" + WHITELIST_LENGTH + "]" + value);
            }
            whitelistLength = StringToInt(value.trim(), whitelistLength);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("not match");
            }
        }

    }

}
