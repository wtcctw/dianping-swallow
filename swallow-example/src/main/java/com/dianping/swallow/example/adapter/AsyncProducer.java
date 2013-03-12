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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dianping.swallow.Destination;
import com.dianping.swallow.MQService;
import com.dianping.swallow.MQService.ProducerOptionKey;
import com.dianping.swallow.MessageProducer;
import com.dianping.swallow.producer.adapter.MQServiceAdapter;

public class AsyncProducer {

    public static String MONGO_URI = "192.168.8.21:27017";
    public static Destination dest = Destination.topic("example");

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MQServiceAdapter sqs = new MQServiceAdapter(MONGO_URI);

        Map<ProducerOptionKey, Object> pOptions = new HashMap<ProducerOptionKey, Object>();
        pOptions.put(ProducerOptionKey.MsgSendRetryCount, MQService.PRODUCR_RETRY_COUNT_FOREVER);
        Map<String, Object> pOptions0_6 = new HashMap<String, Object>();
//        pOptions0_6.put("filequeueBaseDir", "/data/appdatas/filequeue/abc");
        MessageProducer p = sqs.createProducer(dest, pOptions, pOptions0_6);
        for (int i = 0; i < 10; i++) {
            p.send(p.createStringMessage(new Date() + "  -" + i));
        }
        Thread.sleep(20000);
    }

}
