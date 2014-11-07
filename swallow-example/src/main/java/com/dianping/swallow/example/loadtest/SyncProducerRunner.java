package com.dianping.swallow.example.loadtest;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class SyncProducerRunner {

    public static final int QPS           = 20;  //每个Producer发送消息的qps
    private static int      topicCount    = 1;
    private static int      producerCount = 10;
    private static final 	int  totalCount = 50000;
//  private static final 	int  totalCount = 10;
    private static AtomicInteger count = new AtomicInteger();
    private static final Logger LOGGER       = LoggerFactory.getLogger(SyncProducerRunner.class);

    public static void main(String[] args) throws Exception {
    	
    	CountDownLatch latch = new CountDownLatch(topicCount*producerCount);
    	long startTime = System.currentTimeMillis();
        for (int i = 0; i < topicCount; i++) {
            String topic = "LoadTestTopic-" + i;
            for (int j = 0; j < producerCount; j++) {
                ProduceRunner runner = new ProduceRunner(topic, latch);
                runner.start();
            }
        }
        latch.await();
        System.out.println("send rate:" + totalCount/((System.currentTimeMillis() - startTime)/1000));
        
    }

    static class ProduceRunner extends Thread {

    	private String topic;
    	private CountDownLatch latch;
        private ProduceRunner(String topic, CountDownLatch latch) {
        	
            this.topic = topic;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                ProducerConfig config = new ProducerConfig();
                config.setMode(ProducerMode.SYNC_MODE);
                Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);

                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if(count.get() >= totalCount){
                    	break;
                    }
                    int currentCount = count.incrementAndGet();
                    try {
                        String msg = currentCount + ";" + new Date();
                        producer.sendMessage(msg);
                        if(currentCount%1000 == 0){
                        	System.out.println("send:" + currentCount);
                        }
                        //                        System.out.println("Sended msg:" + msg);
//                        Thread.sleep(1000 / QPS);//每个Producer发送消息的20qps
                    } catch (Exception e) {
                    	LOGGER.error(e.getMessage() + ", msg:" + currentCount, e);
                    }
                }
            } catch (RemoteServiceInitFailedException e1) {
                e1.printStackTrace();
            }finally{
            	latch.countDown();
            }
        }
    }

}
