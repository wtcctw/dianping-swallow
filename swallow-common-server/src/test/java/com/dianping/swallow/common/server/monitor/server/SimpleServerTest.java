package com.dianping.swallow.common.server.monitor.server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.dianping.swallow.common.internal.util.CommonUtils;

/**
 * @author mengwenchao
 *
 * 2015年9月10日 下午3:23:34
 */
public class SimpleServerTest {
	
	
	@Test
	public void testHttpServer() throws ClientProtocolException, IOException, InterruptedException{
		
		HttpClient httpClient = createHttpClient();
		
		for(;;){
			
			HttpResponse response = httpClient.execute(new HttpPost("http://192.168.218.22:2222"));
			System.out.println(EntityUtils.toString(response.getEntity()));
			
			TimeUnit.SECONDS.sleep(8);
		}
		
		
		
		
	}

	
	private HttpClient createHttpClient() {
		
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(CommonUtils.getCpuCount()));
        ConnManagerParams.setTimeout(params, 1000);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        
        return httpClient;
	}

}
