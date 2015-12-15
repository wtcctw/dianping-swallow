package com.dianping.swallow.common.internal.dao.impl;
	

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig.CHANGED_BEHAVIOR;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig.CHANGED_ITEM;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig.SwallowConfigArgs;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigDistributed;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.dao.DAOContainer;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.util.SwallowMatcher;

/**
 * @author mengwenchao
 *
 * 2015年11月5日 上午10:49:55
 */
public class DefaultMessageDaoFactoryTest extends AbstractDbTest{

	private DefaultMessageDaoFactory defaultMessageDaoFactory;

	private String[] clusters = new String[]{"mongodb://192.168.213.143:27018,192.168.213.143:27118,192.168.213.143:27218", 
			"mongodb://127.0.0.1:27018,127.0.0.1:27118,127.0.0.1:27218"};

	@Mock
	private ClusterManager clusterManager;
	
	
	private SwallowConfigDistributed swallowConfig;

	
	@Override
	protected void doBeforeAbstractDbTest() {
		System.getProperties().setProperty("SWALLOW.STORE.LION.CONFFILE", "swallow-store-lion-daofactory.properties");
	}
	
	@After
	public void afterDefaultMessageDaoFactoryTest(){
		
	}
	/**
	#192.168.31.178:27016
	swallow.mongo.heartbeatServerURI=mongodb://192.168.213.143:27018
	swallow.topiccfg.default={"mongoUrl":"mongodb://192.168.213.143:27018","size":100,"max":100}
	swallow.topiccfg.topic1={"size":100,"max":100}
	swallow.topiccfg.topic2={}
	swallow.topiccfg.topic3={"mongoUrl":"mongodb://192.168.213.143:27118","size":101,"max":102}
	swallow.topiccfg.topic4={"mongoUrl":"mongodb://127.0.0.1:27118","size":101,"max":102}
	 * @throws Exception
	 */
	@Before
	public void beforeDefaultMessageDaoFactoryTest() throws Exception{
		
		defaultMessageDaoFactory = new DefaultMessageDaoFactory();
		
		swallowConfig = (SwallowConfigDistributed) getSwallowConfig();
		initClusterManager();
		
		defaultMessageDaoFactory.setSwallowConfig(swallowConfig);
		defaultMessageDaoFactory.setClusterManager(clusterManager);

		defaultMessageDaoFactory.initialize();;

	}
	
	@Test
	public void testcreateAllTopicDao() throws ClusterCreateException{
		
		
		Map<String, DAOContainer<MessageDAO<?>>> daos = defaultMessageDaoFactory.getDaos();
		Assert.assertEquals(3, daos.size());
		
		Assert.assertNull(daos.get("topic2"));
		
	}

	@Test
	public void testDefault() throws ClusterCreateException{

		//delete Default
		try{
			defaultMessageDaoFactory.update(null, new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, AbstractSwallowConfig.TOPICNAME_DEFAULT, CHANGED_BEHAVIOR.DELETE));
			Assert.fail();
		}catch(Exception e){
			e.printStackTrace();
		}

		//update Default
		
		try{
			swallowConfig.putConfig(AbstractSwallowConfig.TOPICNAME_DEFAULT, new TopicConfig("mongodb://127.0.0.1:27118", 1, 1));
			
			defaultMessageDaoFactory.update(null, new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, AbstractSwallowConfig.TOPICNAME_DEFAULT, new TopicConfig()));
			Assert.fail();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testAdd() throws ClusterCreateException{
		
		String topicName = testName.getMethodName() + "-1";

		Assert.assertNull(defaultMessageDaoFactory.getDaos().get(topicName));
		
		swallowConfig.putConfig(topicName, new TopicConfig("mongodb://127.0.0.1:27018", 1, 1));
		
		defaultMessageDaoFactory.update(null, new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, topicName, CHANGED_BEHAVIOR.ADD));
		
		
		Assert.assertNotNull(defaultMessageDaoFactory.getDaos().get(topicName));
		

		String topicName2 = testName.getMethodName() + "-2";

		Assert.assertNull(defaultMessageDaoFactory.getDaos().get(topicName2));

		swallowConfig.putConfig(topicName2, new TopicConfig());
		defaultMessageDaoFactory.update(null, new SwallowConfigArgs(CHANGED_ITEM.TOPIC_STORE, topicName2, CHANGED_BEHAVIOR.ADD));

		Assert.assertNull(defaultMessageDaoFactory.getDaos().get(topicName2));

		
		
	}
	
	@Test
	public void testUpdate() throws Exception{
		
		
		System.out.println(clusterManager.getCluster("mongodb://127.0.0.1:27018").allServers());
		System.out.println(clusterManager.getCluster("mongodb://127.0.0.1:27118").allServers());
		
		String topicName = "topic3";
		
		//有到无
		Assert.assertNotNull(defaultMessageDaoFactory.getDaos().get(topicName));
		swallowConfig.onConfigChange(SwallowConfigDistributed.topicKey(topicName), new TopicConfig().toJson());
		Assert.assertNull(defaultMessageDaoFactory.getDaos().get(topicName));
		
		//无到有
		swallowConfig.onConfigChange(SwallowConfigDistributed.topicKey(topicName), new TopicConfig("mongodb://127.0.0.1:27018", 1, 1).toJson());
		
		DAOContainer<?> dao = defaultMessageDaoFactory.getDaos().get(topicName);
		Assert.assertNotNull(dao);
		
		//同一个集群,不同seed
		swallowConfig.onConfigChange(SwallowConfigDistributed.topicKey(topicName), new TopicConfig("mongodb://127.0.0.1:27118", 1, 1).toJson());
		Assert.assertEquals(dao, defaultMessageDaoFactory.getDaos().get(topicName));
		
	}

	private void initClusterManager() throws ClusterCreateException {

		for(final String cluster : clusters){

			final List<InetSocketAddress> allServers = getServerList(cluster);
			
			when(clusterManager.getCluster(argThat(new SwallowMatcher<String>() {

				@Override
				public boolean matches(Object item) {
					
					if(item == null){
						return false;
					}
					
					String url = (String) item;
					List<InetSocketAddress> servers = getServerList(url);
					for(InetSocketAddress addr : servers){
						if(allServers.contains(addr)){
							return true;
						}
					}
					return false;
				}
			}))).then(new Answer<Cluster>() {

				@Override
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public Cluster answer(InvocationOnMock invocation) throws Throwable {
					
					String url = (String) invocation.getArguments()[0];
					
					for(ClusterFactory factory : factorys){
						
						if(factory.accepts(url)){
							
							Cluster real = factory.createCluster(url);
							
							Cluster cluster = mock(real.getClass());
							
							MessageDAO messageDao = mock(MessageDAO.class);
							
							when(cluster.createMessageDao()).thenReturn(messageDao);
							when(cluster.getSeeds()).thenReturn(real.getSeeds());
							when(cluster.allServers()).thenReturn(allServers);
							when(cluster.sameCluster((Cluster) anyObject())).then(new Answer<Boolean>() {

								@Override
								public Boolean answer(InvocationOnMock invocation) throws Throwable {
									
									Cluster thisObject = (Cluster) invocation.getMock();
									Cluster thatObject = (Cluster) invocation.getArguments()[0];
									
									return AbstractCluster.sameCluster(thisObject, thatObject);
								}
							});
							return cluster;
						}
					}
					return null;
				}
			});
		}
	}

	private List<InetSocketAddress> getServerList(String url) {

		Cluster result = null;
		for(ClusterFactory factory : factorys){
			if(factory.accepts(url)){
				result = factory.createCluster(url);
			}
		}

		return result.getSeeds();
	}
	
	
	@Test
	public void testCreateAllTopicDao(){
		
		defaultMessageDaoFactory.createAllTopicDao();
	}
}
