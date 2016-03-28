package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author qi.yin
 *         2016/03/28  下午2:52.
 */
public class MessageSizeProcessor extends AbstractProcessor implements Processor, ConfigChangeListener {

    private static final String LOCAL_MESSAGE_CONFIG_FILENAME = "swallow-producerclient-message.properties";
    private static final String MESSAGE_SIZE_THRESHOLD_KEY = "swallow.producer.client.messageSizeThreshold";

    DynamicConfig dynamicConfig = new DefaultDynamicConfig(LOCAL_MESSAGE_CONFIG_FILENAME);

    private volatile long sizeThreshold = 100000;

    public MessageSizeProcessor() {
        String strValue = dynamicConfig.get(MESSAGE_SIZE_THRESHOLD_KEY);
        sizeThreshold = Integer.parseInt(strValue);
        dynamicConfig.addConfigChangeListener(this);
    }

    @Override
    public void beforeOnMessage(SwallowMessage message) throws SwallowException {

    }

    @Override
    public void afterOnMessage(SwallowMessage message) throws SwallowException {

    }

    @Override
    public void beforeSend(SwallowMessage message) throws SwallowException {
        if (message.size() > sizeThreshold) {
            throw new SwallowException("message size over threshold " + sizeThreshold + ".");
        }
    }

    @Override
    public void onConfigChange(String key, String value) throws Exception {
        if (MESSAGE_SIZE_THRESHOLD_KEY.equals(key)) {
            sizeThreshold = Integer.parseInt(value);
        }
    }
}
