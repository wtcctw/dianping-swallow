package com.dianping.swallow.common.internal.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.kafka.KafkaClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoClusterFactory;

/**
 * @author mengwenchao
 *
 * 2015年10月13日 下午2:01:16
 */
public abstract class AbstractDbTest extends AbstractTest{
	
	
	protected ClusterFactory []factorys = new ClusterFactory[]{new MongoClusterFactory(), new KafkaClusterFactory()};
	
	protected Properties serverProperties;
	
	private String mongoAddress;
	
	private String kafkaAddress;

	@Before
	public void beforeAbstractDbTest() throws IOException{
		
		serverProperties = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("test-server.properties");
		serverProperties.load(ins);
		
		
		mongoAddress = serverProperties.getProperty("mongoAddress");
		kafkaAddress = serverProperties.getProperty("kafkaAddress");


	}
	
	protected String getMongoAddress(){
		
		return mongoAddress;
	}

	protected String getKafkaAddress(){
		
		return kafkaAddress;
	}

}
