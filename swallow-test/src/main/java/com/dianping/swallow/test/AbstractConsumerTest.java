package com.dianping.swallow.test;


/**
 * @author mengwenchao
 *
 * 2015年3月18日 下午5:34:49
 */
public abstract class AbstractConsumerTest extends AbstractTest{

	private String consumerId = "st";
	

	protected String getConsumerId() {
		
		return consumerId + "-" + testName.getMethodName();
	}

	
}
