package com.dianping.swallow.broker.service.impl;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;

public class ConsumerWrap implements MessageListener {

    private String   url;

    private String   consumerId;

    private String   topic;

    private Consumer consumer;

    //收到消息后，使用HttpClient将消息发给url (url，topic，consumer 组合不能重复)

    public ConsumerWrap(String url, String consumerId, String topic) {
        super();
        this.url = url;
        this.consumerId = consumerId;
        this.topic = topic;
    }

    public void start() {
        consumer.setListener(this);
        consumer.start();
    }

    @Override
    public void onMessage(Message msg) throws BackoutMessageException {
        //调用url
        invoke(url,msg);
    }

    private void invoke(String url2, Message msg) {
        // TODO Auto-generated method stub
        
    }

}
