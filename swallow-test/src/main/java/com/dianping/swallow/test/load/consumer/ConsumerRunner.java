package com.dianping.swallow.test.load.consumer;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.util.PropertiesUtils;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.test.load.AbstractLoadTest;
import com.dianping.swallow.test.load.BitMarker;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年9月7日 上午11:45:35
 */
public class ConsumerRunner extends AbstractLoadTest{

    private int consumerCount = Integer.parseInt(System.getProperty("consumerCount", "10"));
    
    @JsonIgnore
    private Map<String, BitMarker> messageCount = new ConcurrentHashMap<String, BitMarker>();
    
    private boolean differentConsumerId = Boolean.parseBoolean(System.getProperty("differentConsumerId", "true"));
    
    private String consumerIdPrefix = PropertiesUtils.getProperty("consumerIdPrefix", "myid");
    
    public static void main(String[] args) throws Exception {
    	
    	new ConsumerRunner().start();
    }

    @Override
	protected void doStart() {
    	if(logger.isInfoEnabled()){
    		logger.info("[doStart]" + this);
    	}
		startReceiver();
	}
    @Override
	protected boolean isExitOnExecutorsReturn() {
    	
		return false;
	}
    @SuppressWarnings("deprecation")
	private void startReceiver() {

        String rawConsumerId = getConsumerId();

        for (int i = 0; i < topicCount; i++) {
            final String topic = getTopicName(topicName, topicStartIndex + i);
            for (int j = 0; j < consumerCount; j++) {
                ConsumerConfig config = new ConsumerConfig();
                if(j == (consumerCount - 1)){
                	config.setMessageFilter(MessageFilter.createInSetMessageFilter(type));
                }
                config.setThreadPoolSize(concurrentCount);
                config.setRetryCountOnBackoutMessageException(0);
                
                final String consumerId = differentConsumerId? (rawConsumerId + "-" + j): rawConsumerId;
                Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId, config);
                c.setListener(new MessageListener() {
                	
                	BitMarker bitMarker = getBitMarker(topic, consumerId);
                	
                    @Override
                    public void onMessage(Message msg) {
                    	
                    	count(msg, bitMarker);
                    	increaseAndGetCurrentCount();
                    }
                });
                c.start();
            }
        }
	}


	private synchronized BitMarker getBitMarker(String topic, String consumerId) {
		
		String key = "topic-" + consumerId;
		BitMarker bitMarker = messageCount.get(key);
		
		if(bitMarker == null){
			bitMarker = new BitMarker();
			messageCount.put(key, bitMarker);
		}	return bitMarker;
	}

	private void count(Message msg, BitMarker bm) {

		long count = Long.parseLong(msg.getContent().split(";")[0]);
		bm.mark(count);
	}


	private String getConsumerId() {
		
		if(consumerIdPrefix != null){
			return consumerIdPrefix;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");
		return "myid-" + format.format(new Date());
	}

	
	@Override
	protected void doOnExit() {

		for(Entry<String, BitMarker> entry : messageCount.entrySet()){

			BitMarker bitMarker = entry.getValue();
			logger.info("[doOnExit]" + entry.getKey() + "," + bitMarker.realCount() + "," + bitMarker.noRepetCount());
		}
	}
}