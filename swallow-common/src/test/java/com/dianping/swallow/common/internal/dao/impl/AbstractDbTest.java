package com.dianping.swallow.common.internal.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年10月13日 下午2:01:16
 */
public abstract class AbstractDbTest extends AbstractTest{
	
	
	
	protected Properties serverProperties;

	@Before
	public void beforeAbstractDbTest() throws IOException{
		
		serverProperties = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("test-server.properties");
		serverProperties.load(ins);

	}
	
	protected abstract String getDbAddress();

}
