package com.dianping.swallow.client.nuclear.consumer.impl;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

/**
 * @author qi.yin
 *         2015/12/15  上午11:21.
 */
public class NuclearConsumerFactory implements ConsumerFactory {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String APP_FILENAME = "META-INF/app.properties";

    private String appKey = "";

    private boolean isOnline = EnvUtil.isProduct() ? true : false;

    public NuclearConsumerFactory() {
        initAppKey();
    }

    public NuclearConsumerFactory(boolean isOnline) {
        this();
        this.isOnline = isOnline;
    }

    public NuclearConsumerFactory(String appKey, boolean isOnline) {
        this.appKey = appKey;
        this.isOnline = isOnline;
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {
        Consumer consumer = new NuclearConsumer(appKey, dest, consumerId, isOnline, config);
        return consumer;
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId) {
        return createConsumer(dest, consumerId, new NuclearConsumerConfig());
    }

    @Override
    public Consumer createConsumer(Destination dest, ConsumerConfig config) {
        throw new UnsupportedOperationException("[createConsumer] unsupported this operation.");
    }


    @Override
    public Consumer createConsumer(Destination dest) {
        throw new UnsupportedOperationException("[createConsumer] unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getTopicAddress(String topic){
        throw new UnsupportedOperationException("[getTopicAddress] unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic){
        throw new UnsupportedOperationException("[getOrDefaultTopicAddress] unsupported this operation.");
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    private void initAppKey() {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_FILENAME);
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                appKey = properties.getProperty("app.name");
                if (logger.isInfoEnabled()) {
                    logger.info("[initAppKey] find appKey:" + appKey + " in " + APP_FILENAME + ".");
                }
            } else {
                logger.error("[initAppKey] cannot find  " + APP_FILENAME + ".");
            }
        } catch (IOException e) {
            logger.error("[initAppKey] read " + APP_FILENAME + " failed.", e);
        }
    }
}
