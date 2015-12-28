package com.dianping.swallow.common.server.monitor;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.util.http.HttpManager;
import com.dianping.swallow.common.internal.util.http.SimpleHttpResponse;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public abstract class AbstractMonitorDataTest extends AbstractTest{
	
	
	protected HttpManager httpManager;
	
	protected String ip = "127.0.0.1";
	
	@Before
	public void beforeMonitorDataTest(){
		
		httpManager = new HttpManager();
		
	}



	@Test
	public void testSerialize(){
	
		MonitorData monitorData = createMonitorData();
		String json = monitorData.jsonSerialize();
		
		System.out.println(json);
		
		MonitorData monitorData2 = MonitorData.jsonDeSerialize(json, getMonitorClass());
		
		Assert.assertEquals(monitorData, monitorData2);
	}
	
	
	@Test
	public void testBuildTotal(){
		
		MonitorData monitorData = createMonitorData();
		addMessages(monitorData);
		monitorData.buildTotal();
		checkTotal(monitorData);
	}

	protected abstract void checkTotal(MonitorData monitorData);

	protected abstract void addMessages(MonitorData monitorData);

	@Test
	public void sendData() throws ClientProtocolException, IOException{
		
		if(!testLocalWebServer()){
			logger.warn("[sendData][local server down]");
			return;
		}
		
		HttpPost post = createPost();
		
		try{
			if(logger.isInfoEnabled()){
				logger.info("[sendProducerData][begin]");
			}
			SimpleHttpResponse<String> response = httpManager.executeReturnString(post);
			if(logger.isInfoEnabled()){
				logger.info("[sendProducerData][result]" + response);
			}
		}catch(Exception e){
			logger.error("[sendData]", e);
			post.abort();
		}finally{
			
		}
	}

	private HttpPost createPost() throws UnsupportedEncodingException {
		
		HttpPost post = new HttpPost(getUrl());
		String json = createMonitorData().jsonSerialize();
		if(logger.isInfoEnabled()){
			logger.info("[createPost]" + json);
		}
		StringEntity request = new StringEntity(json, ContentType.APPLICATION_JSON);
		post.setEntity(request);
		return post;
	}

	
	protected abstract String getUrl();

	protected abstract MonitorData createMonitorData();
	
	protected abstract Class<? extends MonitorData> getMonitorClass();

}
