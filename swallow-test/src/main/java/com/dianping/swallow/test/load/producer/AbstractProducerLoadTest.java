package com.dianping.swallow.test.load.producer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;
import com.dianping.swallow.test.load.AbstractLoadTask;
import com.dianping.swallow.test.load.AbstractLoadTest;

/**
 * @author mengwenchao
 *
 * 2015年1月26日 下午10:00:50
 */
public abstract class AbstractProducerLoadTest extends AbstractLoadTest{

    private int 		sendMessageInterval = Integer.parseInt(System.getProperty("sendMessageInterval", "0"));
    private boolean		zipped = Boolean.parseBoolean(System.getProperty("zipped", "false"));

	protected ProducerMode getProducerMode() {
		
		return ProducerMode.SYNC_MODE;
	}
    
	@Override
	protected Runnable createLoadTask(String topicName, int concurrentIndex) {
		
		String messageType = isLastTopic(topicName) ? type : null;
		
		logger.info("[createLoadTask][messageType]" + messageType);
		
		return new ProduceRunner(topicName, concurrentIndex, messageType);
	}
    


	class ProduceRunner  extends AbstractLoadTask {

    	private String type;
        private ProduceRunner(String topic, int concurrentIndex, String type) {
        	super(topic, concurrentIndex);
            this.type = type;
        }

        @Override
        public void doRun() {
            try {
            	
                ProducerConfig config = new ProducerConfig();
                ProducerMode mode = getProducerMode();
                if(logger.isInfoEnabled()){
                	logger.info("[run]" + mode);
                }
                config.setMode(mode);
                config.setZipped(zipped);
                Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topicName), config);

                while(true){
                    long currentCount = increaseAndGetCurrentCount();

                    boolean retry = false;
                    int retryCount = 0;
                    do{
	                    try {
	                    	
	                        String msg = currentCount + ";" + new Date() + message;
	                        if(type == null || currentCount%100000 > 10){
	                        	producer.sendMessage(msg);
	                        }else{
	                        	
	                        	producer.sendMessage(msg, type);
	                        }
	                        retry = false;
	                    } catch (Exception e) {
	                    	retry = true;
	                    	retryCount++;
	                    	logger.error(e.getMessage() + ", msg:" + currentCount + ", retry:" + retryCount, e);
	                    	sleep(100);
	                    }
                    }while(retry == true);
                    
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
