package com.dianping.swallow.web.alarmer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:14
 */
public abstract class AbstractServiceAlarmer extends AbstractAlarmer {

	protected Map<String, Boolean> lastCheckStatus = new HashMap<String, Boolean>();

	protected static String SERVER_CHECK_URL_FILE = "server-check-url.properties";

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	protected EventReporter eventReporter;
	
	@Autowired
	protected EventFactory eventFactory;

	protected void threadSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logger.error("[threadSleep] interrupted.", e);
		}
	}

	protected HttpResult checkUrl(String url) {
		HttpResult result = httpSerivice.httpGet(url);
		if (!result.isSuccess()) {
			threadSleep();
			result = httpSerivice.httpGet(url);
		}
		if (!result.isSuccess()) {
			threadSleep();
			result = httpSerivice.httpGet(url);
		}
		return result;
	}

}
