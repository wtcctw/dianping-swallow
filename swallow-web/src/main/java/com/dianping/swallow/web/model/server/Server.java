package com.dianping.swallow.web.model.server;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
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

	private static Logger logger = LoggerFactory.getLogger(Server.class);

	protected static String SERVER_CHECK_URL_FILE = "server-check-url.properties";

	protected static String pigeonHealthUrlFormat = "http://{ip}:4080/stats.json";

	private static final String PIGEON_HEALTH_URL_KEY = "pigeonHealthUrl";

	private static final String SLAVE_MONITOR_URL_KEY = "slaveMonitorUrl";

	protected static String slaveMonitorUrlFormat = "http://{ip}:8080/names";

	static {
		initProperties();
	}

	private String ip;

	private boolean lastStatus;

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

	private static void initProperties() {
		try {
			InputStream in = ProducerServer.class.getClassLoader().getResourceAsStream(SERVER_CHECK_URL_FILE);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					pigeonHealthUrlFormat = StringUtils.trim(prop.getProperty(PIGEON_HEALTH_URL_KEY));
					slaveMonitorUrlFormat = StringUtils.trim(prop.getProperty(SLAVE_MONITOR_URL_KEY));
				} finally {
					in.close();
				}
			} else {
				logger.info("[initProperties] Load {} file failed.", SERVER_CHECK_URL_FILE);
				throw new RuntimeException();
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", SERVER_CHECK_URL_FILE);
			throw new RuntimeException(e);
		}
	}

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
