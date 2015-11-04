package com.dianping.swallow.common.internal.dao.impl.mongodb;



import jmockmongo.MockMongo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.dianping.swallow.AbstractSpringTest;

public abstract class AbstractDAOImplTest extends AbstractSpringTest {

	private static MockMongo mock;

	protected static final String IP = "127.0.0.1";

	@Before
	public void beforeAbstractDAOImplTest(){
		
	}
	
	
	
	@Override
	protected String getApplicationContextFile() {
		
		return "applicationContext-test.xml";
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
		mock.stop();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

}
