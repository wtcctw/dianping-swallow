package com.dianping.swallow.common.internal.monitor;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.monitor.data.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public abstract class AbstractMonitorDataTest extends AbstractTest{
	
	
	protected ClientConnectionManager connectionManager;
	
	protected HttpClient httpClient;
	
	@Before
	public void beforeMonitorDataTest(){
	
		connectionManager = new PoolingClientConnectionManager();
		httpClient = new DefaultHttpClient(connectionManager);
		
	}

	
	@Test
	public void testSerialize(){
	
		MonitorData monitorData = createMonitorData();
		String json = monitorData.jsonSerialize();
		
		System.out.println(json);
		
		MonitorData monitorData2 = MonitorData.jsonDeSerialize(json, getMonitorClass());
		
		Assert.assertEquals(monitorData, monitorData2);
	}


	protected abstract MonitorData createMonitorData();
	
	protected abstract Class<? extends MonitorData> getMonitorClass();

}
