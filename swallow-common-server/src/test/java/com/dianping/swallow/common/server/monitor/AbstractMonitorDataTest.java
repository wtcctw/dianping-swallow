package com.dianping.swallow.common.server.monitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.collector.ContentType;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public abstract class AbstractMonitorDataTest extends AbstractTest{
	
	
	protected HttpClient httpClient;
	
	protected String ip = "127.0.0.1";
	
	@Before
	public void beforeMonitorDataTest(){
	
		httpClient = createHttpClient();;
		
	}

	private HttpClient createHttpClient() {
		
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(CommonUtils.getCpuCount()));
        ConnManagerParams.setTimeout(params, 5000);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        return httpClient;
        
		
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
		
		HttpResponse response;
		try{
			if(logger.isInfoEnabled()){
				logger.info("[sendProducerData][begin]");
			}
			response = httpClient.execute(post);
			if(logger.isInfoEnabled()){
				logger.info("[sendProducerData][result]" + EntityUtils.toString(response.getEntity()));
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
		StringEntity request = new StringEntity(json, HTTP.UTF_8);
		request.setContentType(ContentTypeDesc.APPLICATION_JSON);
		post.setEntity(request);
		return post;
	}

	
	protected abstract String getUrl();

	protected abstract MonitorData createMonitorData();
	
	protected abstract Class<? extends MonitorData> getMonitorClass();

}
