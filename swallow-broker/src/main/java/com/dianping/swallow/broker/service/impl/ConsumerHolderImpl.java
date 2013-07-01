package com.dianping.swallow.broker.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.broker.service.ProducerHolder;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

@Service
public class ConsumerHolderImpl {
    private static final Logger   LOG         = LoggerFactory.getLogger(ConsumerHolderImpl.class);

    private Map<String, Consumer> producerMap = new ConcurrentHashMap<String, Consumer>();

    @Autowired
    private DynamicConfig         dynamicConfig;

    public void init() throws RemoteServiceInitFailedException {
        //获取<url>,<cid>,<topic>;<url>,<cid>,<topic>;配置项
        String consumerStr = dynamicConfig.get("swallow.broker.consumers");
        String[] topics = StringUtils.split(consumerStr, ';');
        LOG.info("initing producers with topics(" + Arrays.toString(topics) + ")");

        //每个配置项创建一个ConsumerWrap,然后启动BrokerConsumer（BrokerConsumer负责启动所有consumer,接受消息，并发给url）
        
        
        //每个topic创建一个consumer
        if (topics != null) {
            for (String topic : topics) {
                ConsumerConfig config = new ConsumerConfig();
                config.setThreadPoolSize(1);
                Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic),
                        "swallow-broker", config);
                c.setListener(new MessageListener() {
                    @Override
                    public void onMessage(Message msg) {
                        System.out.println(msg.getContent());
                        //            System.out.println(msg.transferContentToBean(MsgClass.class));
                    }
                });
                c.start();
            }
        }

    }

}
