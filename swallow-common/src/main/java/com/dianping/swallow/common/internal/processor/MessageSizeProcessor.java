package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author qi.yin
 *         2016/03/28  下午2:52.
 */
public class MessageSizeProcessor extends AbstractProcessor implements Processor, ConfigChangeListener {

    private static final String PRODUCERCLIENT_CONFIG_FILENAME = "swallow-producerclient.properties";
    private static final String MESSAGE_SIZE_THRESHOLD_KEY = "swallow.producer.client.messageSizeThreshold";

    private DynamicConfig dynamicConfig = new DefaultDynamicConfig(PRODUCERCLIENT_CONFIG_FILENAME);

    private volatile long sizeThreshold = 50000;

    public MessageSizeProcessor() {
        String strValue = dynamicConfig.get(MESSAGE_SIZE_THRESHOLD_KEY);
        if (StringUtils.isEmpty(strValue)) {
            throw new IllegalArgumentException("messageSizeThreshold is null.");
        }

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
        if (message.getContent() != null && (message.getContent().length() * 2) / 1000 > sizeThreshold) {
            throw new SwallowException("message size over threshold " + sizeThreshold + "kb.");
        }
    }

    @Override
    public void onConfigChange(String key, String value) throws Exception {
        if (MESSAGE_SIZE_THRESHOLD_KEY.equals(key)) {
            if (!StringUtils.isEmpty(value)) {
                sizeThreshold = Integer.parseInt(value);
            }
        }
    }
}
