package com.dianping.swallow.test;

import org.junit.Before;


/**
 * @author mengwenchao
 *
 * 2015年3月18日 下午5:34:49
 */
public abstract class AbstractConsumerTest extends AbstractSwallowTest{

	private String consumerId = "st";
	
	
	@Before
	public void beforeSimpleTest(){
		
		mdao.cleanMessage(topic, getConsumerId());
		mdao.cleanMessage(topic, null);
	}

	protected String getConsumerId() {
		
		return consumerId + "-" + testName.getMethodName();
	}

	
}
