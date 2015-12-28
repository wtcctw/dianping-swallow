package com.dianping.swallow.common.internal.util.http;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * @author mengwenchao
 *
 * 2015年12月24日 下午6:01:48
 */
public class HttpManagerTest extends AbstractHttpTest{
	
	private  HttpManager httpManager;
	
	
	private int port;
	
	private String address;
	
	@Before
	public void beforeHttpManagerTest(){
		
		httpManager = new HttpManager(1000, 1000, 1, true);
		port = startSimpleHttpServer();
		address = "http://localhost:" + port; 
		
	}
	
	@Test
	public void testSimple() throws UnsupportedEncodingException{
		
		logger.info(URLEncoder.encode("中国", "utf-8"));

	}
	
	@Test
	public void testGet() throws IOException{
		
		HttpGet get = new HttpGet(address);

		SimpleHttpResponse<String> response = httpManager.executeReturnString(get);
		
		if(logger.isInfoEnabled()){
			logger.info("[testGet]" + response);
		}
		
	}
	
	@Test
	public void testPost() throws IOException{
		
		
		HttpPost post = new HttpPost(address);
		
		List<NameValuePair> parameters = new LinkedList<NameValuePair>();
		parameters.add(new BasicNameValuePair("a", "1"));
		parameters.add(new BasicNameValuePair("b", "中国"));
		HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
		
		post.setEntity(entity);
		
		SimpleHttpResponse<String> response = httpManager.executeReturnString(post);
		
		if(logger.isInfoEnabled()){
			logger.info("[testPost]" + response);
		}
	}
	
	
	@Test
	public void testExecuteConcurrent() throws InterruptedException{
		
		int concurrentCount = 10;
		
		final CountDownLatch latch = new CountDownLatch(concurrentCount);
		
		for(int i=0; i < concurrentCount; i++){
			
			executors.execute(new Runnable(){
				@Override
				public void run() {
		
					SimpleHttpResponse<String> response = null;
					try {
						response = httpManager.executeReturnString(new HttpGet(address));
						if(logger.isInfoEnabled()){
							logger.info("[testExecute]" +  response);
						}
					} catch (IOException e) {
						logger.error("[run]" + address, e);
					}finally{
						latch.countDown();
					}
				}
			});
		}
		
		
		latch.await();
		Assert.assertEquals(1, getConnectionCount(port));
		
		
	}
	
	
	@Test
	public void testExecute() throws IOException{
		
		
		for(int i=0; i < 10 ;i++){
			
			
			SimpleHttpResponse<String> response = httpManager.executeReturnString(new HttpGet(address));
			
			if(logger.isInfoEnabled()){
				logger.info("[testExecute]" +  response);
			}
			
			Assert.assertEquals(1, getConnectionCount(port));
		}
	
	}

	
}
