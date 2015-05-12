package com.dianping.swallow.common.server.monitor.collector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.lifecycle.AbstractLifecycle;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.server.monitor.MonitorActionWrapper;
import com.dianping.swallow.common.server.monitor.data.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 下午2:08:18
 */
public abstract class AbstractCollector extends AbstractLifecycle implements Collector, Runnable, ConfigChange{
	
	private ScheduledExecutorService scheduled;
	
	private boolean turnOff = false;

	protected static final String THREAD_POOL_NAME = "Monitor-Collector-Thread-Pool";
	
	public static final int SEND_INTERVAL = 5;
	
	protected SwallowActionWrapper actionWrapper = new MonitorActionWrapper();
	
	private ScheduledFuture<?> future; 
	
	private HttpClient httpClient;
	protected final int maxWaitConnectionTime = 5000;
	
	protected static final int maxRetryTimesOnException = 3;
	protected static final int maxRetryIntervalOnException = 1000;
	
	private Set<String> excludeTopics;
	private ConfigCache configCache;
	public static final String SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY = "swallow.monitor.exclude.topic"; 

	
	@Override
	public void doInitialize() throws Exception {
		
		scheduled = Executors.newScheduledThreadPool(CommonUtils.getCpuCount(), new MQThreadFactory(THREAD_POOL_NAME));
		createHttpClient();
		
		configCache = ConfigCache.getInstance();
		configCache.addChange(this);
		initExculdeTopics(); 
	}
	
	private void initExculdeTopics() {
		
		try {
			String value = configCache.getProperty(SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY);
			if(logger.isInfoEnabled()){
				logger.info("[getExculdeTopics][exclude]" + value);
			}
			excludeTopics = splitExcludeTopics(value);
		} catch (LionException e) {
			logger.error("[getExculdeTopics]", e);
		}
	}

	private HashSet<String> splitExcludeTopics(String value) {
		
		HashSet<String> excludeTopics = new HashSet<String>(StringUtils.splitByComma(value));
		if(logger.isInfoEnabled()){
			logger.info("[splitExcludeTopics]" + excludeTopics);
		}
		return excludeTopics;
	}

	private void createHttpClient() {
		
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(CommonUtils.getCpuCount()));
        ConnManagerParams.setTimeout(params, maxWaitConnectionTime);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(connectionManager, params); 
		
	}

	@Override
	public void doStart() throws Exception {
		if(turnOff){
			if(logger.isInfoEnabled()){
				logger.info("[doStart][monitor turned off]");
			}
			return;
		}
		future = scheduled.scheduleWithFixedDelay(this, SEND_INTERVAL, SEND_INTERVAL, TimeUnit.SECONDS);
	}
	
	protected boolean isExclude(String topic) {
		
		return excludeTopics.contains(topic);
	}

	
	@Override
	protected void doStop() throws Exception {
		if(turnOff){
			if(logger.isInfoEnabled()){
				logger.info("[doStop][monitor turned off]");
			}
			return;
		}
		future.cancel(false);
	}
	
	@Override
	public void doDispose() throws Exception {
		
		scheduled.shutdownNow();
	}
	
	@Override
	public void run() {
		
		try{
			if(logger.isDebugEnabled()){
				logger.debug("[run][begin]");
			}
			doSendTask();
		}catch(Throwable th){
			logger.error("[run]", th);
		}finally{
			if(logger.isDebugEnabled()){
				logger.debug("[run][end]");
			}
		}
	}

	private void doSendTask() throws UnsupportedEncodingException {

		boolean success = false;
		int retryTimes = 0;
		
		do{
			
			HttpPost post = createPost();
			try{
				HttpResponse response = httpClient.execute(post);
				success = checkResponse(response);
			}catch(Exception e){
				post.abort();
				logger.error("[doSendTask]", e);
			}finally{
				
			}
			
			if(!success){
				if(logger.isInfoEnabled()){
					logger.info("[doSendTask][fail retry]" + retryTimes);
				}
				retryTimes++;
				sleep(maxRetryIntervalOnException);
			}
			
		}while(!success && retryTimes <= maxRetryTimesOnException);
		
		if(!success){
			logger.error("[fail send data]");
		}
		
	}
	
	private void sleep(int sleepIntervalMili) {
		
		try {
			TimeUnit.MILLISECONDS.sleep(sleepIntervalMili);
		} catch (InterruptedException e) {
		}
	}

	private boolean checkResponse(HttpResponse response) throws ParseException, IOException {
		
		String result = EntityUtils.toString(response.getEntity());

		if(response.getStatusLine().getStatusCode() == 200){
			if(logger.isDebugEnabled()){
				logger.debug("[checkResponse]" + response.getStatusLine() + "," + result);
			}
			return true;
		}
		logger.error("[checkResponse][error]" + response.getStatusLine() + "," + result);
		return false;
	}

	private HttpPost createPost() throws UnsupportedEncodingException {
		
		HttpPost post = new HttpPost(getUrl());
		String json = getMonitorData().jsonSerialize();
		if(logger.isDebugEnabled()){
			logger.debug("[createPost]" + json);
		}
		StringEntity request = new StringEntity(json, HTTP.UTF_8);
		request.setContentType(ContentType.APPLICATION_JSON);
		post.setEntity(request);
		return post;
	}

	protected abstract MonitorData getMonitorData();

	protected abstract String getServerType();

	private String getUrl() {
		
		return EnvUtil.getWebAddress() + "/api/stats/" + getServerType();
	}

	
	public boolean isTurnOff() {
		return turnOff;
	}

	public void setTurnOff(boolean turnOff) {
		this.turnOff = turnOff;
	}

	
	@Override
	public void onChange(String key,String value){
		
		if(key != null && key.equals(SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY)){
			if(logger.isInfoEnabled()){
				logger.info("[onChange]["+ SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY +"]" + value);
			}
			excludeTopics = splitExcludeTopics(value);
		}
		
	}

	
}
