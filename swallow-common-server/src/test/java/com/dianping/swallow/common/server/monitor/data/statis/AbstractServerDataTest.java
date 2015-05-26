package com.dianping.swallow.common.server.monitor.data.statis;

import org.junit.Before;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 上午10:40:57
 */
public abstract class AbstractServerDataTest extends AbstractTest{
	
	
	protected Long startKey = 100L, endKey = 400L;
	
	protected String []topics = new String[]{"topic1", "topic2"};
	
	protected String []ips = new String[]{"127.0.0.1", "127.0.0.2"};
	
	protected final long avergeDelay = 50;
	protected final long qpsPerUnit = 10;
	protected final int intervalCount = 6;

	@Before
	public void test(){
		
	}

}
