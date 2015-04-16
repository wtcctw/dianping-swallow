package com.dianping.swallow.common.internal.monitor;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.dianping.swallow.common.internal.monitor.data.MonitorData;
import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;
import com.dianping.swallow.common.internal.util.IPUtil;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public class ProducerMonitorDataTest extends AbstractMonitorDataTest{

	
	
	
	protected Class<? extends MonitorData> getMonitorClass() {
		
		return ProducerMonitorData.class;
	}

	@Test
	public void sendProducerData() throws ClientProtocolException, IOException{
		
		HttpPost post = createPost();
		
		HttpResponse response;
		try{
			response = httpClient.execute(post);
			if(logger.isInfoEnabled()){
				logger.info("[sendProducerData][result]" + EntityUtils.toString(response.getEntity()));
			}
		}finally{
			post.releaseConnection();
		}
	}

	private HttpPost createPost() {
		
		HttpPost post = new HttpPost("http://localhost:8080//api/stats/producer");
		String json = getJsonString();
		if(logger.isInfoEnabled()){
			logger.info("[createPost]" + json);
		}
		StringEntity request = new StringEntity(json, ContentType.APPLICATION_JSON);
		post.setEntity(request);
		return post;
	}

	protected String getJsonString() {
		
		MonitorData monitorData = createMonitorData();
		
		return monitorData.jsonSerialize();
	}

	
	protected MonitorData createMonitorData() {
		
		ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
		producerMonitorData.addData("topic", 1L, System.currentTimeMillis() - 100, System.currentTimeMillis());
		
		return producerMonitorData;
		
	}

}
