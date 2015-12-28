package com.dianping.swallow.common.internal.util.http;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.dianping.swallow.common.internal.util.CommonUtils;

/**
 * 统一的httpClient，长连接服务
 * @author mengwenchao
 *
 * 2015年12月24日 下午5:22:45
 */
public class HttpManager implements Closeable{
	
	
	public static int DEFAULT_SO_TIMEOUT = 5000;
	
	public static int DEFAULT_CONNECTION_TIMEOUT = 5000;
	
	protected final Logger logger  = Logger.getLogger(getClass());

	private CloseableHttpClient httpClient;
	
	private HttpClientContext   defaultHttpClientContext;
	
	private int soTimeout;
	
	private int connectionTimeOut;
	
	private int maxPerRoute;

	private boolean keepalive;

	public HttpManager(){
		this(DEFAULT_SO_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT, CommonUtils.getCpuCount(), true);
	}
	
	public HttpManager(int soTimeout, int connectionTimeout, int maxPerRoute){
		this(soTimeout, connectionTimeout, maxPerRoute, true);
	}
	
	public HttpManager(int soTimeout, int connectionTimeout, int maxPerRoute, boolean keepalive){
		
		this.soTimeout  = soTimeout;
		this.connectionTimeOut = connectionTimeout;
		this.maxPerRoute = maxPerRoute;
		this.keepalive = keepalive;
		
		createHttpClient();
	}
	
	private void createHttpClient(){
		
    	SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(soTimeout).build();
    	
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(maxPerRoute);
        cm.setDefaultSocketConfig(socketConfig);

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setConnectionReuseStrategy(new ConnectionReuseStrategy() {
					
					@Override
					public boolean keepAlive(HttpResponse response, HttpContext context) {
						return keepalive;
					}
				}).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
					
					@Override
					public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
						return -1;
					}
				}).build();
        

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeOut).build();
        defaultHttpClientContext = HttpClientContext.create();
        defaultHttpClientContext.setRequestConfig(requestConfig);

	}

	public SimpleHttpResponse<String> executeReturnString(HttpUriRequest request) throws IOException{
		
		return executeReturnString(request, defaultHttpClientContext);
	}

	
	public SimpleHttpResponse<String> executeReturnString(HttpUriRequest request ,HttpClientContext clientContext) throws IOException{
		
		CloseableHttpResponse response = execute(request, clientContext);
		
		String result = EntityUtils.toString(response.getEntity());
		
		return new SimpleHttpResponse<String>(result, response.getStatusLine());
		
	}
	
	
	/**
	 * 调用完成后，请关闭response
	 * @param request
	 * @param clientContext
	 * @return
	 * @throws IOException
	 */
	protected CloseableHttpResponse execute(HttpUriRequest request, HttpClientContext clientContext) throws IOException{
		
		
		return httpClient.execute(request, clientContext);
		
	}


	@Override
	public void close() throws IOException {
		httpClient.close();
	}
	
}
