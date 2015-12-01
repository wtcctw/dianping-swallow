package com.dianping.swallow.test;



import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;


/**
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:29:16
 */
public class AbstractUnitTest extends AbstractTest{
	
	
	@Rule
	public TestName  testName = new TestName();

	
	@Before
	public void beforeAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[beforeAbstractTest]----------------------------------" + testName.getMethodName());
		}
		
	}

	
	
	@After
	public void afterAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[afterAbstractTest]----------------------------------" + testName.getMethodName());
		}
	}
}
