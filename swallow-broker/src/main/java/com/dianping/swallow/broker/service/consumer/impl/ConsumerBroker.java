package com.dianping.swallow.broker.service.consumer.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.broker.conf.Constant;
import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.util.AppUtils;
import com.dianping.swallow.broker.util.HttpClientUtil;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.google.gson.Gson;

public class ConsumerBroker implements MessageListener {
    private static final Logger  LOG               = LoggerFactory.getLogger(ConsumerBroker.class);

    private NotifyService        notifyService;

    private volatile boolean     active            = false;

    private final ConsumerConfig config;

    private final String         consumerId;

    private final String         topic;

    private Consumer             consumer;

    private String               url;

    private int                  retryCount        = Integer.MAX_VALUE;

    private String               logPrefix;

    private DefaultPullStrategy  pullStrategy;

    private final static int     DELAY_BASE        = 50;                                           //ms

    private final static int     DELAY_UPPER_BOUND = 10000;                                          //ms

    //收到消息后，使用HttpClient将消息发给url (url，topic，consumer 组合不能重复)
    public ConsumerBroker(String topic, String consumerId, String url, ConsumerConfig config, int delayBase,
                          int delayUpperbound) {
        super();
        this.url = url;
        this.consumerId = consumerId;
        this.topic = topic;
        this.config = config;
        this.logPrefix = AppUtils.highlight(topic + "," + consumerId);

        pullStrategy = new DefaultPullStrategy(delayBase, delayUpperbound);
    }

    //收到消息后，使用HttpClient将消息发给url (url，topic，consumer 组合不能重复)
    public ConsumerBroker(String topic, String consumerId, String url, ConsumerConfig config) {
        this(topic, consumerId, url, config, DELAY_BASE, DELAY_UPPER_BOUND);
    }

    public boolean isActive() {
        return active;
    }

    public void start() {
        if (!active) {
            synchronized (this) {
                if (!active) {
                    LOG.info(logPrefix + "Starting");
                    consumer = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId,
                            config);
                    consumer.setListener(this);
                    consumer.start();
                    active = true;
                }
            }
        }
    }

    public void close() {
        if (active) {
            synchronized (this) {
                if (active) {
                    LOG.info(logPrefix + "Closing");
                    consumer.close();
                    active = false;
                }
            }
        }
    }

    @Override
    public void onMessage(Message msg) throws BackoutMessageException {
        //调用url
        try {
            int count = 0;
            boolean success = false;
            do {
                try {
                    if (count > 0) {
                        pullStrategy.fail(true);

                        if (count == 1) {//开始重试时，打印消息，重试过程不打印消息了，减少log
                            LOG.info(logPrefix + "Retrying sending message to url(" + url + "), message is: " + msg
                                    + ", content is:" + msg.getContent());
                        }
                        LOG.info(logPrefix + "Retrying " + count + " times...");
                    }

                    LOG.info(logPrefix + "Sending to url(" + url + "): " + msg + ", content:" + msg.getContent());
                    invoke(msg);

                    success = true;
                    pullStrategy.succeess();
                } catch (IOException e) {//可恢复异常，自己重试，增加重试次数的配置项
                    //失败了(IO)，重试
                    LOG.error(logPrefix + "IO Error when send http message, will be retryed: " + e.getMessage());
                }
            } while (!success && count++ < retryCount);
        } catch (RuntimeException e) {//不可恢复异常，记录以及报警，跳过消息
            LOG.error(logPrefix + "Error when send http message to " + url + ". This message is skiped:" + msg
                    + ", content:" + msg.getContent(), e);
            if (notifyService != null) {
                notifyService.alarm(logPrefix + "Error when send http message to " + url + ", message is skiped.", e,
                        true);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void invoke(Message msg) throws IOException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Constant.CONTENT, msg.getContent()));
        nvps.add(new BasicNameValuePair(Constant.TOPIC, topic));
        Map<String, String> properties = msg.getProperties();
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                nvps.add(new BasicNameValuePair(name, value));
            }
        }
        //http调用，发送消息
        String result = HttpClientUtil.post(url, nvps);
        Gson gson = new Gson();
        Map resultMap = gson.fromJson(result, Map.class);
        if (resultMap == null || StringUtils.equalsIgnoreCase(String.valueOf(resultMap.get("result")), "success")) {
            //http响应成功了，但结果不对，则记录
            throw new RuntimeException(logPrefix + "Error(result is null or success not true), result is " + result);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getTopic() {
        return topic;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setNotifyService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "ConsumerBroker [active=" + active + ", url=" + url + ", consumerId=" + consumerId + ", topic=" + topic
                + "]";
    }

}
