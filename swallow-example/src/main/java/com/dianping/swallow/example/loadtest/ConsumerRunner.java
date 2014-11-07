package com.dianping.swallow.example.loadtest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class ConsumerRunner {

    private int topicCount    = 1;
    private int consumerCount = 10;
    private AtomicInteger count = new AtomicInteger();
    private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);
    private long lastTime;
    private long lastCount;
    private Map<Integer, AtomicInteger> statis = new ConcurrentHashMap<Integer, AtomicInteger>();
    private final Logger LOGGER       = LoggerFactory.getLogger(getClass());

    
    public static void main(String[] args) throws Exception {
    	new ConsumerRunner().start();
    }

	private void start() {
		startCounter();
		startReceiver();
	}

	private void startCounter() {
		
		lastTime = System.currentTimeMillis();
		lastCount = 0;
		
		scheduled.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				long currentTime = System.currentTimeMillis();
				int currentCount = count.get();
				long rate = (currentCount - lastCount)/((currentTime - lastTime)/1000);
				LOGGER.info("rate:" + rate + "/s");
				LOGGER.info("total:" + currentCount);
				lastCount = currentCount;
				lastTime = currentTime;
			}
			
		}, 2, 2, TimeUnit.SECONDS);
	}

	private void startReceiver() {
        for (int i = 0; i < topicCount; i++) {
            String topic = "LoadTestTopic-" + i;
            for (int j = 0; j < consumerCount; j++) {
                ConsumerConfig config = new ConsumerConfig();
                //以下两项根据自己情况而定，默认是不需要配的
                config.setThreadPoolSize(2);
                config.setRetryCountOnBackoutMessageException(0);
                Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), "myId-20130813", config);
                c.setListener(new MessageListener() {
                    @Override
                    public void onMessage(Message msg) {
                    	
                    	count.incrementAndGet();
                    	Integer key = Integer.parseInt(msg.getContent().split(";")[0]);
                    	synchronized (this) {
                        	if(statis.get(key) == null){
                        		statis.put(key, new AtomicInteger());
                        	}
						}
                    	AtomicInteger atomic = statis.get(key);
                    	atomic.incrementAndGet();
                    }
                });
                c.start();
            }
        }
	}

}
