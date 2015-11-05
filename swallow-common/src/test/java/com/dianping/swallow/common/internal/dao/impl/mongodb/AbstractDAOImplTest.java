package com.dianping.swallow.common.internal.dao.impl.mongodb;




import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.dianping.swallow.AbstractSpringTest;

public abstract class AbstractDAOImplTest extends AbstractSpringTest {


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
		
	}
	
	@After
	public void afterAbstractDAOImplTest() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

}
