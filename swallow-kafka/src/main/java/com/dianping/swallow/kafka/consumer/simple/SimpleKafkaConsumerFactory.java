package com.dianping.swallow.kafka.consumer.simple;

import java.net.InetSocketAddress;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import kafka.javaapi.consumer.SimpleConsumer;

/**
 * @author mengwenchao
 *
 * 2015年11月27日 下午7:48:32
 */
public class SimpleKafkaConsumerFactory extends  BaseKeyedPooledObjectFactory<InetSocketAddress, SimpleConsumer>  implements KeyedPooledObjectFactory<InetSocketAddress, SimpleConsumer>{

	protected final Logger logger = LogManager.getLogger(getClass());

	private int soTimeout;
	private int bufferSize;
	private String  clientId;
	
	public SimpleKafkaConsumerFactory(int soTimeout, int bufferSize, String clientId) {
		
		this.soTimeout = soTimeout;
		this.bufferSize = bufferSize;
		this.clientId = clientId;
	}

	@Override
	public SimpleConsumer create(InetSocketAddress key) throws Exception {
		
		SimpleConsumer simpleConsumer = new SimpleConsumer(key.getHostName(), key.getPort(), soTimeout, bufferSize, clientId);;
		if(logger.isInfoEnabled()){
			logger.info("[create]" + key + "," + soTimeout + "," + bufferSize + "," + clientId + "," + simpleConsumer);
		}
		
		return simpleConsumer;
	}

	@Override
	public PooledObject<SimpleConsumer> wrap(SimpleConsumer value) {
		return new DefaultPooledObject<SimpleConsumer>(value);
	}

	@Override
	public void destroyObject(InetSocketAddress key, PooledObject<SimpleConsumer> p) throws Exception {
		
		if(logger.isInfoEnabled()){
			logger.info("[destroyObject]" + key + p);
		}

	}
	

}
