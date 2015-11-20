package com.dianping.swallow.web.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * @author mingdongli
 *
 * 2015年10月15日下午6:34:41
 */
public class Work implements Runnable {
	private String topic;
	private CountDownLatch beginSignal;
	private CountDownLatch endSignal;

	public Work(String topic, CountDownLatch begin, CountDownLatch end) {
		this.topic = topic;
		this.beginSignal = begin;
		this.endSignal = end;
	}
	
	public void httpPost(String url) throws JSONException, ClientProtocolException, IOException {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);  
        // 接收参数json列表  
        JSONObject jsonParam = new JSONObject();  
        jsonParam.put("topic", topic);
        jsonParam.put("approver", "hongjun.zhong");
        jsonParam.put("size", "10");
        jsonParam.put("amount", "5"); 
        jsonParam.put("applicant", "yapu.wang"); 
        jsonParam.put("test", "true");
        jsonParam.put("type", "一般消息队列");
          
        StringEntity entity = new StringEntity(jsonParam.toString(),"UTF-8");  
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");    
        method.setEntity(entity);

		System.out.println(jsonParam);
		HttpResponse result = httpClient.execute(method);
        
        String resData = EntityUtils.toString(result.getEntity());
        System.out.println(resData);
	}

	@Override
	public void run() {
		try {

			beginSignal.await();

			try {
				httpPost("http://localhost:8080/api/topic/apply");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			endSignal.countDown();
			System.out.println("work" + topic + "finish");
		}
	}
}
