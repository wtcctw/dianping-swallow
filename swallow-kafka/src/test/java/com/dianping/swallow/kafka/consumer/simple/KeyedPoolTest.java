package com.dianping.swallow.kafka.consumer.simple;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.kafka.AbstractKafkaTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.javaapi.consumer.SimpleConsumer;

/**
 * @author mengwenchao
 *
 * 2015年11月27日 下午9:35:01
 */
public class KeyedPoolTest extends AbstractKafkaTest{

	private String host = "127.0.0.1";
	private int port = 11111;

	@Test
	public void test() throws JsonProcessingException{
		
		GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
		
		ObjectMapper objectMapper = new ObjectMapper();
		if(logger.isInfoEnabled()){
			logger.info(objectMapper.writeValueAsString(config));
		}
	}
	
	@Test
	public void testFactory() throws Exception{
		
		
		SimpleKafkaConsumerFactory factory = new SimpleKafkaConsumerFactory(1000, 1000, "test");
		PooledObject<SimpleConsumer> object = factory.makeObject(new InetSocketAddress(host, port));
		factory.destroyObject(new InetSocketAddress(host, port), object);
		
	}
	
	@Test
	public void testPool() throws NoSuchElementException, IllegalStateException, Exception{

		int maxTotalPerKey = 10;
		
		GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
		config.setMaxTotalPerKey(maxTotalPerKey);
		config.setMaxIdlePerKey(1);
		config.setBlockWhenExhausted(true);
		config.setMaxWaitMillis(10);
		
		KeyedObjectPool<InetSocketAddress, SimpleConsumer> consumerPool = new GenericKeyedObjectPool<InetSocketAddress, SimpleConsumer>(
				new  SimpleKafkaConsumerFactory(1000, 1000, testName.getMethodName()), config);
		
		List<SimpleConsumer> consumers = new LinkedList<SimpleConsumer>();
		
		for(int i=0; i < maxTotalPerKey;i++){
			consumers.add(consumerPool.borrowObject(new InetSocketAddress(host, port)));
		}
		
		try{
			//can not borrow
			consumerPool.borrowObject(new InetSocketAddress(host, port));
			Assert.fail();
		}catch(NoSuchElementException e){
			
		}
		
		for(SimpleConsumer consumer : consumers){
			consumerPool.returnObject(new InetSocketAddress(consumer.host(), consumer.port()), consumer);
		}
		
		Assert.assertEquals(0, consumerPool.getNumActive());
		Assert.assertEquals(1, consumerPool.getNumIdle());
		
		
		
		try{
			consumerPool.returnObject(new InetSocketAddress(host, port), null);
			Assert.fail();
		}catch(Exception e){
			
		}
	}
	
	@Test
	public void testHash(){
		System.out.println(System.identityHashCode(null));
	}
}
