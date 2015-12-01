package com.dianping.swallow.common.internal.dao.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.kafka.KafkaClusterFactory;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoClusterFactory;
import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月2日 下午6:59:01
 */
public class DefaultClusterManagerTest extends AbstractTest{

	private DefaultClusterManager clusterManager;
	
	private List<Lifecycle> lifecycle = new LinkedList<Lifecycle>();

	@Before
	public void beforeDefaultClusterManagerTest() throws Exception{
		clusterManager = createClusterManager();
		
	}

	@Test
	public void testEquals(){
		
		SwallowMessage message1 = createMessage(1L);
		message1.putInternalProperty("haha", "hahaha");
		SwallowMessage message2 = createMessage(1L);
		
		Assert.assertTrue(equals(message1, message2));
		Assert.assertFalse(equals(message1, message2, true, true));
		
	}
	
	@Test
	public void testCreate() throws ClusterCreateException{
		
		String url1 = "mongodb://127.0.0.1:27017,127.0.0.2:27018", url11 = "mongodb://127.0.0.2:27018,127.0.0.1:27017";
		Cluster cluster1 = clusterManager.getCluster(url1);
		
		Assert.assertEquals(cluster1, clusterManager.getCluster(url1));
		
		Assert.assertEquals(cluster1, clusterManager.getCluster(url11));

		String url2 = "mongodb://127.0.0.1:27017,127.0.0.2:27019";
		
		Cluster cluster2 = clusterManager.getCluster(url2);
		Assert.assertNotEquals(cluster2, cluster1);
		
		Assert.assertEquals(2, clusterManager.allClusters().size());
		
	}
	
	
	

	@After
	public void afterDefaultClusterManagerTest() throws Exception{
		
		for(int i=lifecycle.size() - 1; i >= 0;i--){
			lifecycle.get(i).dispose();
		}
	}

	
	private DefaultClusterManager createClusterManager() throws Exception {
		
		DefaultClusterManager clusterManager = new DefaultClusterManager();
		
		MongoClusterFactory mongoClusterFactory = new MongoClusterFactory();
		mongoClusterFactory.initialize();

		lifecycle.add(mongoClusterFactory);

		KafkaClusterFactory kafkaClusterFactory = new KafkaClusterFactory();
		kafkaClusterFactory.initialize();

		lifecycle.add(kafkaClusterFactory);

		
		List<ClusterFactory> clusterFactories = new LinkedList<ClusterFactory>();
		
		clusterFactories.add(mongoClusterFactory);
		clusterFactories.add(kafkaClusterFactory);
		
		clusterManager.setClusterFactories(clusterFactories);

		lifecycle.add(clusterManager);

		return clusterManager;
	}

}
