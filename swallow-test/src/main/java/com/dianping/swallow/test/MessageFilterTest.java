package com.dianping.swallow.test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;

/**
 * @author mengwenchao
 *
 * 2015年5月13日 下午6:03:03
 */
public class MessageFilterTest extends AbstractConsumerTest{

	protected String type = "filter";
	protected int messageCount = 1000;
	
	
	@Test
	public void filter() throws SendFailedException, RemoteServiceInitFailedException{
		
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		Consumer consumer = addListener(getTopic(), getConsumerId(), filters);
		sendMessage(messageCount/2, getTopic());
		sendMessage(messageCount/2, getTopic(), type);
		
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount/2, getConsumerMessageCount(consumer));
		
	}

	
	
	private volatile Set<String> types = new HashSet<String>();

	@Test
	@SuppressWarnings("unused")
	public void dynamicChange() throws SendFailedException, RemoteServiceInitFailedException{
		

		String type2 = "filter2";
		
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		filters.add(type2);
		
		if(logger.isInfoEnabled()){
			logger.info(types.toString());
		}
		
		Consumer consumer = addListener(getTopic(), getConsumerId(), filters);
		
		sendMessage(messageCount/2, getTopic(), type);
		sendMessage(messageCount/2, getTopic(), type2);
		
		waitForListernToComplete(messageCount);
		System.out.println(types + "," + types.size());
		Assert.assertEquals(2, types.size());

		types.clear();
		filters.remove(type);
		
		Consumer consumer2 = addListener(getTopic(), getConsumerId(), filters);
		sendMessage(messageCount/2, getTopic(), type);
		sendMessage(messageCount/2, getTopic(), type2);
	
		waitForListernToComplete(messageCount);
		Assert.assertEquals(1, types.size());
	}
	
	@Override
	protected  synchronized void doOnMessage(Message msg) {
		
		types.add(msg.getType());
		if(logger.isDebugEnabled()){
			logger.debug(types + "," + msg.getType());
		}
	}
	
	
	/**
	 * 在type为特定类型，而消息表中没有此类消息时，ack继续增加
	 * @throws SendFailedException
	 * @throws RemoteServiceInitFailedException
	 * @throws InterruptedException
	 */
	@Test
	public void testFilterJump() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{

		String type = "type";
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		@SuppressWarnings("unused")
		Consumer consumer = addListener(getTopic(), getConsumerId(), filters);
		
		sendMessage(10, getTopic(), type);

		for(int i=0;i<20;i++){
			sendMessage(10, getTopic());
			sleep(100);
		}

		Long maxMessageId = mdao.getMaxMessageId(getTopic());
		sleep(2000);
		Long maxAck = mdao.getAckMaxMessageId(getTopic(), getConsumerId());
		
		Assert.assertEquals(maxMessageId, maxAck);
		sendMessage(10, getTopic(), type);
		
		sleep(2000);
		maxMessageId = mdao.getMaxMessageId(getTopic());
		maxAck = mdao.getAckMaxMessageId(getTopic(), getConsumerId());
		Assert.assertEquals(maxMessageId, maxAck);
		
	}
	
	@Test
	public void testBackupJump(){
		
		String type = "type";
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		Consumer consumer = addListener(getTopic(), getConsumerId(), filters);
		
		SwallowMessage rightMessage = createSwallowMessage();
		rightMessage.setType(type);
		rightMessage.putInternalProperty(InternalProperties.TOPIC, getTopic());
		rightMessage.putInternalProperty(InternalProperties.CONSUMERID, getConsumerId());

		SwallowMessage wrongMessage = createSwallowMessage();
		wrongMessage.setType(type + "-");

		//some right message
		int rightCount = 10, wrongCount = 1000;
		for(int i=0;i<rightCount;i++){
			mdao.saveMessage(getTopic(), getConsumerId(), rightMessage);
		}
		sleep(3000);
		Assert.assertEquals(rightCount, getConsumerMessageCount(consumer));
		
		//wrong message jump
		for(int i=0;i<wrongCount;i++){
			mdao.saveMessage(getTopic(), getConsumerId(), wrongMessage);
			sleep(1);
		}
		sleep(1000);
		Assert.assertEquals(rightCount, getConsumerMessageCount(consumer));
		
		Long maxMessageId =  mdao.getMaxMessageId(getTopic(), getConsumerId());
		Long maxAckMessageId = mdao.getAckMaxMessageId(getTopic(), getConsumerId(), true);
		
		Assert.assertEquals(maxMessageId, maxAckMessageId);
		

		//right message
		for(int i=0;i<rightCount;i++){
			mdao.saveMessage(getTopic(), getConsumerId(), rightMessage);
		}
		sleep(2000);
		Assert.assertEquals(rightCount*2, getConsumerMessageCount(consumer));
		
		maxMessageId =  mdao.getMaxMessageId(getTopic(), getConsumerId());
		maxAckMessageId = mdao.getAckMaxMessageId(getTopic(), getConsumerId(), true);
		
		Assert.assertEquals(maxMessageId, maxAckMessageId);
	}

	public SwallowMessage createSwallowMessage() {
		
		SwallowMessage message = new SwallowMessage();
		message.setContent("this is a SwallowMessage");
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");

		message.putInternalProperty(InternalProperties.SAVE_TIME, String.valueOf(System.currentTimeMillis() - 50));
		return message;
	}
}
