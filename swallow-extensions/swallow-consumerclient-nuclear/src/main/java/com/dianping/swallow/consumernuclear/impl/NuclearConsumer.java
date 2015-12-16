package com.dianping.swallow.consumernuclear.impl;

import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener;
import com.dianping.swallow.consumer.internal.action.RetryOnAllExceptionActionWrapper;
import com.dianping.swallow.consumer.internal.action.RetryOnBackoutMessageExceptionActionWrapper;
import com.meituan.nuclearmq.client.error.MQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qi.yin
 *         2015/12/14  下午2:57.
 */
public class NuclearConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(NuclearConsumer.class);

    private static final String SWALLOW_APPKEY = "swallow";

    //private static final String SWALLOW_APPKEY = "mtpoiop";

    private com.meituan.nuclearmq.client.Consumer consumer = null;

    private Destination dest;

    private String consumerId;

    private ConsumerConfig config;

    private MessageListener listener;

    private PullStrategy pullStrategy;

    private volatile AtomicBoolean started = new AtomicBoolean(false);

    public NuclearConsumer(Destination dest, String consumerId, ConsumerConfig config) {

        checkArgument(dest, consumerId, config);
        this.dest = dest;
        this.consumerId = consumerId;
        this.config = config;
        consumer = com.meituan.nuclearmq.client.Consumer.Factory.create();

        consumer.setAppkey(SWALLOW_APPKEY);
        consumer.setTopic(dest.getName());
        consumer.setGroup(consumerId);
        consumer.setIsAsync(false);
        consumer.setIsOnline(EnvUtil.isProduct());
        this.pullStrategy = new DefaultPullStrategy(config.getDelayBaseOnBackoutMessageException(),
                config.getDelayUpperboundOnBackoutMessageException());
        consumer.setCallback(new NuclearMessageListener(this, createRetryWrapper()));
    }

    @Override
    public Destination getDest() {
        return dest;
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }

    @Override
    public void start() {
        if (listener == null) {
            throw new IllegalArgumentException(
                    "MessageListener is null, MessageListener should be set(use setListener()) before start.");
        }
        if (started.compareAndSet(false, true)) {
            if (logger.isInfoEnabled()) {
                logger.info("Starting " + this.toString());
            }
            try {
                consumer.start();
            } catch (MQException e) {
                logger.error("[start] consumer start failed.", e);
            }
        }
    }

    @Override
    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public MessageListener getListener() {
        return listener;
    }

    @Override
    public void close() {
        if (started.compareAndSet(true, false)) {

            if (logger.isInfoEnabled()) {
                logger.info("Closing " + this.toString());
            }
            consumer.stop();
            consumer.join();
            consumer.close();
        }
    }

    @Override
    public boolean isClosed() {
        return !started.get();
    }

    @Override
    public void update(Observable observable, Object args) {
        throw new UnsupportedOperationException();
    }

    private void checkArgument(Destination dest, String consumerId, ConsumerConfig consumerConfig) {

        if (StringUtils.isEmpty(consumerId)) {
            throw new IllegalArgumentException("ConsumerId should not be null.");
        }
    }

    private SwallowCatActionWrapper createRetryWrapper() {

        if (listener instanceof MessageRetryOnAllExceptionListener) {
            return new RetryOnAllExceptionActionWrapper(pullStrategy, config.getRetryCount());
        }
        return new RetryOnBackoutMessageExceptionActionWrapper(pullStrategy, config.getRetryCount());
    }

    @Override
    public String toString() {
        return "NuclearConsumer[" +
                "dest=" + dest +
                ", consumerId='" + consumerId + '\'' +
                ", config=" + config +
                ']';
    }
}
