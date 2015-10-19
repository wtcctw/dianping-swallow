package com.dianping.swallow.web.model.server;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:41:56
 */
public class ProducerServer extends Server {

	private String pigeonHealthUrl = "http://{ip}:4080/stats.json";

	private boolean isServiceLastAlarmed;

	public ProducerServer(String ip) {
		eventType = EventType.PRODUCER;
		isServiceLastAlarmed = false;
		this.setIp(ip);
	}

	public void initServer() {
		super.initServer();
		if (StringUtils.isNotBlank(serverConfig.getPigeonHealthUrl())) {
			pigeonHealthUrl = StringUtils.replace(serverConfig.getPigeonHealthUrl(), "{ip}", ip);
		}
	}

	public void checkService() {
		HttpResult httpResult = requestUrl(pigeonHealthUrl);
		if (httpResult.isSuccess() && isServiceLastAlarmed) {
			report(ip, ip, ServerType.PIGEON_SERVICE_OK);
			isServiceLastAlarmed = false;
		} else {
			report(ip, ip, ServerType.PIGEON_SERVICE);
			isServiceLastAlarmed = true;
		}
	}

	@Override
	public String toString() {
		return "ProducerServer [pigeonHealthUrl=" + pigeonHealthUrl + ", isServiceLastAlarmed=" + isServiceLastAlarmed
				+ "] " + super.toString();
	}

}
