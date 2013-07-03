package com.dianping.swallow.broker.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.broker.conf.Constant;
import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.util.HttpClientUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

public class ConsumerBroker implements MessageListener {
    private static final Logger LOG    = LoggerFactory.getLogger(ConsumerBroker.class);

    private NotifyService       notifyService;

    private volatile boolean    active = false;

    private String              url;

    private String              consumerId;

    private String              topic;

    private Consumer            consumer;

    //收到消息后，使用HttpClient将消息发给url (url，topic，consumer 组合不能重复)
    public ConsumerBroker(String topic, String consumerId, String url) {
        super();
        this.url = url;
        this.consumerId = consumerId;
        this.topic = topic;

        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(1);
        consumer = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId, config);
        consumer.setListener(this);
    }

    public boolean isActive() {
        return active;
    }

    public void start() {
        if (!active) {
            synchronized (consumer) {
                if (!active) {
                    consumer.start();
                    active = true;
                }
            }
        }
    }

    public void close() {
        if (!active) {
            synchronized (consumer) {
                if (!active) {
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
            invoke(msg);
        } catch (IOException e) {
            throw new BackoutMessageException("Error when send http message to " + url, e);
        } catch (Exception e) {
            LOG.error("Error when send http message to " + url, e);
            if (notifyService != null) {
                notifyService.alarm("Error when send http message to " + url, e, true);
            }
        }
    }

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
        HttpClientUtil.post(url, nvps);
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

}
