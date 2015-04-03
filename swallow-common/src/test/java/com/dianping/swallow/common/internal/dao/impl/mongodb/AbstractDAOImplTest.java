package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.concurrent.TimeUnit;

import jmockmongo.MockMongo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:applicationContext-test.xml")
public abstract class AbstractDAOImplTest extends AbstractJUnit4SpringContextTests {

	protected static final String TOPIC_NAME = "topicForUnitTest";
	protected static final String CONSUMER_ID = "consumer1";
	protected static final String IP = "127.0.0.1";

	@Autowired
	private DefaultMongoManager mongoClient;

	private static MockMongo mock;

	@Rule
	public TestName  testName = new TestName();

	@Before
	public void beforeAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[-----------------][begin test]" + testName.getMethodName());
		}
	}


	
	@BeforeClass
	public synchronized static void setUpClass() throws Exception {
		if (mock == null) {
			mock = new MockMongo(24521);
			mock.start();
		}
	}

	@After
	public void afterAbstractDAOImplTest() throws Exception {
		// 删除测试过程创建的Collection
		mongoClient.getMessageCollection(TOPIC_NAME).drop();
		mongoClient.getAckCollection(TOPIC_NAME, CONSUMER_ID).drop();
		mongoClient.getHeartbeatCollection(IP.replace('.', '_')).drop();
		mock.stop();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	protected void sleep(int mili) {
		try {
			TimeUnit.MILLISECONDS.sleep(mili);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void afterAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[-----------------][end test]" + testName.getMethodName());
		}
	}
	
}
