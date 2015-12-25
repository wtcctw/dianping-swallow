package com.dianping.swallow.client.nuclear.consumer.impl;

import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener;
import com.dianping.swallow.consumer.internal.action.RetryOnAllExceptionActionWrapper;
import com.dianping.swallow.consumer.internal.action.RetryOnBackoutMessageExceptionActionWrapper;
import com.dianping.swallow.consumer.internal.task.LongTaskChecker;
import com.dianping.swallow.consumer.internal.task.TaskChecker;
import com.meituan.nuclearmq.client.error.MQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qi.yin
 *         2015/12/14  下午2:57.
 */
public class NuclearConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(NuclearConsumer.class);

    private static final String APPKEY_PREFIX = "com.dianping.swallow.";

    private com.meituan.nuclearmq.client.Consumer consumer = null;

    private Destination dest;

    private String consumerId;

    private ConsumerConfig config;

    private MessageListener listener;

    private TaskChecker taskChecker;

    private PullStrategy pullStrategy;

    private ExecutorService helperExecutors;

    private volatile AtomicBoolean started = new AtomicBoolean(false);

    public NuclearConsumer(String appKey, Destination dest, String consumerId, boolean isOnline, ConsumerConfig config) {

        checkArgument(consumerId);

        this.dest = dest;
        this.consumerId = consumerId;
        this.config = config;
        consumer = com.meituan.nuclearmq.client.Consumer.Factory.create();

        consumer.setAppkey(APPKEY_PREFIX + appKey);
        consumer.setTopic(dest.getName());
        consumer.setGroup(consumerId);
        consumer.setIsOnline(isOnline);
        if (config instanceof NuclearConsumerConfig) {
            consumer.setIsAsync(((NuclearConsumerConfig) config).isAsync());
        } else {
            consumer.setIsAsync(false);
        }

        this.pullStrategy = new DefaultPullStrategy(config.getDelayBaseOnBackoutMessageException(),
                config.getDelayUpperboundOnBackoutMessageException());

        this.taskChecker = new LongTaskChecker(config.getLongTaskAlertTime(), "NuclearMQ.SwallowLongTask");

        consumer.setCallback(new NuclearConsumerCallback(this, createRetryWrapper(), taskChecker, new NuclearConsumerProcessor()));
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
                startHelpers();
                startShutdownHook();
            } catch (MQException e) {
                logger.error("[start] consumer start failed.", e);
            }
        }
    }

    private void startShutdownHook() {
        Thread hook = new Thread(new Runnable() {
            @Override
            public void run() {
                if (logger.isInfoEnabled()) {
                    logger.info("Swallow nuclearmq consumer stoping...");
                }
                close();
                if (logger.isInfoEnabled()) {
                    logger.info("Swallow nuclearmq consumer stoped.");
                }
            }
        });
        hook.setDaemon(true);
        hook.setName("Swallow-ShutdownHook-NuclearMQ-" + this.dest.getName());
        Runtime.getRuntime().addShutdownHook(hook);
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
            closeHelpers();
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

    private void checkArgument(String consumerId) {

        if (StringUtils.isEmpty(consumerId)) {
            throw new IllegalArgumentException("ConsumerId should not be empty.");
        }
    }

    private SwallowCatActionWrapper createRetryWrapper() {

        if (listener instanceof MessageRetryOnAllExceptionListener) {
            return new RetryOnAllExceptionActionWrapper(pullStrategy, config.getRetryCount());
        }
        return new RetryOnBackoutMessageExceptionActionWrapper(pullStrategy, config.getRetryCount());
    }

    private void startHelpers() {
        helperExecutors = Executors.newCachedThreadPool(new MQThreadFactory("Swallow-NuclearMQ-Helper-"));
        helperExecutors.execute(taskChecker);
    }

    private void closeHelpers() {
        helperExecutors.shutdown();
        taskChecker.close();
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
