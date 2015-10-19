package com.dianping.swallow.web.model.server;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactoryImpl;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年8月31日 下午4:57:26
 */
public abstract class Server implements Sendable, Serviceable {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static final long SENDER_INTERVAL = 20 * 1000;

	protected EventType eventType;

	private boolean isSendLastAlarmed = false;

	protected String ip;

	protected ServerConfig serverConfig;

	protected HttpService httpService;

	protected EventReporter eventReporter;

	protected EventFactoryImpl eventFactory;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void setEventReporter(EventReporter eventReporter) {
		this.eventReporter = eventReporter;
	}

	public void setEventFactory(EventFactoryImpl eventFactory) {
		this.eventFactory = eventFactory;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public abstract void initServer();

	@Override
	public void checkSender(long sendTimeStamp) {
		if (System.currentTimeMillis() - sendTimeStamp > SENDER_INTERVAL) {
			report(ip, ip, ServerType.SERVER_SENDER);
			isSendLastAlarmed = true;
		} else {
			if (isSendLastAlarmed) {
				report(ip, ip, ServerType.SERVER_SENDER_OK);
			}
			isSendLastAlarmed = false;
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
			count++;
		} while (!result.isSuccess() && count < 3);
		return result;
	}

	protected void threadSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logger.error("[threadSleep] interrupted.", e);
		}
	}

	protected void report(String masterIp, String slaveIp, ServerType serverType) {
		ServerEvent serverEvent = eventFactory.createServerEvent();
		serverEvent.setIp(masterIp).setSlaveIp(slaveIp).setServerType(serverType).setEventType(eventType)
				.setCreateTime(new Date());
		eventReporter.report(serverEvent);
	}

	@Override
	public String toString() {
		return "Server [eventType=" + eventType + ", isSendLastAlarmed=" + isSendLastAlarmed + ", ip=" + ip
				+ ", serverConfig=" + serverConfig + "] " + super.toString();
	}

}
