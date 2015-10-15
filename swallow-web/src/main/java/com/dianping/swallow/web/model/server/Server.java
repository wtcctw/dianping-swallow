package com.dianping.swallow.web.model.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年8月31日 下午4:57:26
 */
public abstract class Server {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private String ip;

	private boolean lastStatus;
	
	protected ServerConfig serverConfig;

	protected HttpService httpService;

	protected EventReporter eventReporter;

	protected EventFactory eventFactory;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setEventReporter(EventReporter eventReporter) {
		this.eventReporter = eventReporter;
	}

	public void setEventFactory(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public abstract void doAlarm();

	protected void threadSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logger.error("[threadSleep] interrupted.", e);
		}
	}

	protected HttpResult requestUrl(String url) {
		int count = 0;
		HttpResult result = null;
		do {
			if (count != 0) {
				threadSleep();
			}
			result = httpService.httpGet(url);
		} while (count < 3 && !result.isSuccess());
		return result;
	}

	public boolean getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(boolean lastStatus) {
		this.lastStatus = lastStatus;
	}

}
