package com.dianping.swallow.common.server.monitor.collector;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.internal.util.http.HttpManager;
import com.dianping.swallow.common.internal.util.http.SimpleHttpResponse;
import com.dianping.swallow.common.server.monitor.MonitorActionWrapper;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 下午2:08:18
 */
public abstract class AbstractCollector extends AbstractLifecycle implements Collector, Runnable, ConfigChangeListener{
	
	private ScheduledExecutorService scheduled;
	
	private boolean turnOff = false;

	protected static final String THREAD_POOL_NAME = "Monitor-Collector-Thread-Pool";
	
	public static final int SEND_INTERVAL = 5;
	
	protected SwallowActionWrapper actionWrapper = new MonitorActionWrapper();
	
	private ScheduledFuture<?> future; 
	
	
	private HttpManager httpManager;
	protected final int maxWaitConnectionTime = 5000;
	protected final int soTimeout = 5000;
	
	protected static final int maxRetryTimesOnException = 3;
	protected static final int maxRetryIntervalOnException = 1000;
	
	private Set<String> excludeTopics;
	private DynamicConfig dynamicConfig;
	public static final String SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY = "swallow.monitor.exclude.topic"; 

	
	@Override
	public void doInitialize() throws Exception {
		
		scheduled = Executors.newScheduledThreadPool(CommonUtils.getCpuCount(), new MQThreadFactory(THREAD_POOL_NAME));
		createHttpManager();
		
		dynamicConfig = new DefaultDynamicConfig();
		dynamicConfig.addConfigChangeListener(this);
		initExculdeTopics(); 
	}
	
	private void initExculdeTopics() {
		
		try {
			String value = dynamicConfig.get(SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY);
			if(logger.isInfoEnabled()){
				logger.info("[getExculdeTopics][exclude]" + value);
			}
			excludeTopics = splitExcludeTopics(value);
		} catch (LionException e) {
			logger.error("[getExculdeTopics]", e);
		}
	}

	private HashSet<String> splitExcludeTopics(String value) {
		
		HashSet<String> excludeTopics = new HashSet<String>();
		
		if(value != null){
			excludeTopics.addAll(StringUtils.splitByComma(value));
		}else{
			logger.warn("[splitExcludeTopics][lion value null]");
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[splitExcludeTopics]" + excludeTopics);
		}
		return excludeTopics;
	}

	private void createHttpManager() {

		httpManager = new HttpManager(soTimeout, maxWaitConnectionTime, CommonUtils.getCpuCount(), true);
		
	}

	@Override
	public void doStart() throws Exception {
		if(turnOff){
			if(logger.isInfoEnabled()){
				logger.info("[doStart][monitor turned off]");
			}
			return;
		}
		future = scheduled.scheduleAtFixedRate(this, SEND_INTERVAL, SEND_INTERVAL, TimeUnit.SECONDS);
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
		
		httpManager.close();
		scheduled.shutdownNow();
	}
	
	@Override
	public void run() {
		
		SwallowActionWrapper cat = new CatActionWrapper(getClass().getSimpleName(), "sendMessage");
		cat.doAction(new SwallowAction() {
			
			@Override
			public void doAction() throws SwallowException {
				try {
					doSendTask();
				} catch (UnsupportedEncodingException e) {
					logger.error("run", e);
				}
			}
		});
	}

	private void doSendTask() throws UnsupportedEncodingException {

		boolean success = false;
		int retryTimes = 0;
		
		do{
			
			HttpPost post = createPost();
			try{
				SimpleHttpResponse<String> response = httpManager.executeReturnString(post);
				success = checkResponse(response);
			}catch(Exception e){
				post.abort();
				
				if(e instanceof SocketException){
					logger.error(e.toString());
				}else{
					logger.error("[doSendTask]", e);
				}
				
				if(e instanceof SocketTimeoutException){
					break;
				}
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

	private boolean checkResponse(SimpleHttpResponse<String> response) throws ParseException, IOException {
		
		if(response.getStatusCode() == 200){
			if(logger.isDebugEnabled()){
				logger.debug("[checkResponse]" + response);
			}
			return true;
		}
		logger.error("[checkResponse][error]" + response);
		return false;
	}

	private HttpPost createPost() throws UnsupportedEncodingException {
		
		HttpPost post = new HttpPost(getUrl());
		
		MonitorData monitorData = getMonitorData();
		monitorData.setCurrentTime(System.currentTimeMillis());
		String json = monitorData.jsonSerialize();
		if(logger.isDebugEnabled()){
			logger.debug("[createPost]" + json);
		}
		StringEntity request = new StringEntity(json, ContentType.APPLICATION_JSON);
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
	public void onConfigChange(String key, String value) throws Exception {
		
		if(key != null && key.equals(SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY)){
			if(logger.isInfoEnabled()){
				logger.info("[onChange]["+ SWLLOW_MONITOR_EXCLUDE_TOPIC_KEY +"]" + value);
			}
			excludeTopics = splitExcludeTopics(value);
		}
		
		
	}
}
