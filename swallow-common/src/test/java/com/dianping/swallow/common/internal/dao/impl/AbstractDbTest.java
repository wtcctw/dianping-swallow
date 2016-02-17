package com.dianping.swallow.common.internal.dao.impl;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Before;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigDistributed;
import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.kafka.KafkaCluster;
import com.dianping.swallow.common.internal.dao.impl.kafka.KafkaClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoCluster;
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
	
	private SwallowServerConfig swallowServerConfig;
	

	@Before
	public void beforeAbstractDbTest() throws Exception{
		
		doBeforeAbstractDbTest();
		
		serverProperties = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("swallow-store-lion.properties");
		serverProperties.load(ins);
		
		loadDbAddress(serverProperties);
		
		swallowServerConfig = new SwallowConfigDistributed();
		swallowServerConfig.initialize();

		if(logger.isInfoEnabled()){
			logger.info("[beforeAbstractDbTest]" + swallowServerConfig);
		}

	}
	
	protected void doBeforeAbstractDbTest() {
		
	}

	private void loadDbAddress(Properties properties) {
		
		for( Entry<Object, Object>  entry : properties.entrySet()){
			
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			
			if(key.startsWith(SwallowConfigDistributed.TOPIC_CFG_PREFIX)){
				
				TopicConfig topicConfig = TopicConfig.fromJson(value);
				String storeUrl = topicConfig.getStoreUrl();
				if(storeUrl != null && storeUrl.startsWith(MongoCluster.schema)){
					this.mongoAddress = storeUrl;
				}
				if(storeUrl != null && storeUrl.startsWith(KafkaCluster.schema)){
					this.kafkaAddress = storeUrl;
				}
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[loadDbAddress]mongo:" + mongoAddress + ", kafka:" + kafkaAddress);
		}

	}

	protected String getMongoAddress(){
		
		return mongoAddress;
	}

	protected String getKafkaAddress(){
		
		return kafkaAddress;
	}

	public SwallowServerConfig getSwallowServerConfig() {
		return swallowServerConfig;
	}

}
