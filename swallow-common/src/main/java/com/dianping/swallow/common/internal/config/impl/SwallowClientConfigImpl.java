package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.common.internal.config.AbstractConfig;
import com.dianping.swallow.common.internal.config.SwallowClientConfig;

/**
 * @author qi.yin
 *         2015/12/16  下午5:29.
 */
public class SwallowClientConfigImpl extends AbstractConfig implements SwallowClientConfig {

    private static final String CLIENT_CONFIG_FILE_NAME = "swallow-client.properties";

    private boolean isLog4j2Enabled = true;

    private boolean isOnMessageLogEnabled = true;

    private static SwallowClientConfig instance = new SwallowClientConfigImpl();

    private SwallowClientConfigImpl() {
        super(CLIENT_CONFIG_FILE_NAME);
        loadConfig();
    }

    public static SwallowClientConfig getInstance() {
        return instance;
    }

    @Override
    public boolean isOnMessageLogEnabled() {
        return isOnMessageLogEnabled;
    }

    @Override
    public boolean isLog4j2Enabled() {
        return isLog4j2Enabled;
    }

}