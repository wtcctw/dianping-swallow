/**
 * Project: ${swallow-client.aid}
 * 
 * File Created at 2011-8-2
 * $Id$
 * 
 * Copyright 2011 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.swallow.example.adapter;

import com.dianping.swallow.MQService;
import com.dianping.swallow.Message;
import com.dianping.swallow.MessageListener;

public class NoDurableConsumer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        MQService sqs = new com.dianping.swallow.consumer.adapter.MQServiceAdapter(AsyncProducer.MONGO_URI);

        //		Map<MQService.ConsumerOptionKey, Object> options = new HashMap<MQService.ConsumerOptionKey, Object>();
        //		options.put(MQService.ConsumerOptionKey.ConsumerID, "durable_subs_2");
        sqs.createConsumer(AsyncProducer.dest, null).setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message msg) {
                System.out.println("NoDurable Consumer Receive: " + msg.getContent());
            }

        });

    }

}
