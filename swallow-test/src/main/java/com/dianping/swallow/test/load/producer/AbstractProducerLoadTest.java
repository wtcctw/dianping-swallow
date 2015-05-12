package com.dianping.swallow.test.load.producer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;
import com.dianping.swallow.test.load.AbstractLoadTest;

/**
 * @author mengwenchao
 *
 * 2015年1月26日 下午10:00:50
 */
public abstract class AbstractProducerLoadTest extends AbstractLoadTest{

    private static int      topicCount    = 1;
    private static int      producerCount = 1;
    private static int  	totalCount = 100;
    private static int 		sendMessageInterval = 0;

	protected static void parseArgs(String[] args) {
		
    	if(args.length >= 1){
    		topicCount = Integer.parseInt(args[0]);
    	}
    	if(args.length >= 2){
    		producerCount = Integer.parseInt(args[1]);
    	}
    	if(args.length >= 3){
    		totalCount = Integer.parseInt(args[2]);
    	}
    	
    	if(args.length >= 4){
    		sendMessageInterval = Integer.parseInt(args[3]);
    	}
		
	}

	protected ProducerMode getProducerMode() {
		
		return ProducerMode.SYNC_MODE;
	}
    
    
    @Override
	protected void doStart() throws InterruptedException {
		    	
    	logger.info("[doStart][args]" + topicCount + "," + producerCount + "," + totalCount + "," + sendMessageInterval);
    	
        for (int i = 0; i < topicCount; i++) {
            String topic = getTopicName(topicName, i);
            for (int j = 0; j < producerCount; j++) {
                ProduceRunner runner = new ProduceRunner(topic);
                executors.execute(runner);
            }
        }
        		
	}

	class ProduceRunner implements Runnable {

    	private String topic;
        private ProduceRunner(String topic) {
            this.topic = topic;
        }

        @Override
        public void run() {
            try {
            	
                ProducerConfig config = new ProducerConfig();
                ProducerMode mode = getProducerMode();
                if(logger.isInfoEnabled()){
                	logger.info("[run]" + mode);
                }
                config.setMode(mode);
                Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);

                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if(count.get() >= totalCount){
                    	break;
                    }
                    int currentCount = count.incrementAndGet();
                    try {
                        String msg = currentCount + ";" + new Date() + message;
                        producer.sendMessage(msg);
                    } catch (Exception e) {
                    	logger.error(e.getMessage() + ", msg:" + currentCount, e);
                    	count.decrementAndGet();
                    }
                    sleep();
                }
            } catch (RemoteServiceInitFailedException e1) {
            	logger.error("[run]", e1);
            }finally{
            	logger.info("[run][exit]");
            }
        }

	}

	private void sleep() {
		if(sendMessageInterval > 0){
			try {
				TimeUnit.MILLISECONDS.sleep(sendMessageInterval);
			} catch (InterruptedException e) {
				logger.error("[sleep]", e);
			}
		}
	}

}
