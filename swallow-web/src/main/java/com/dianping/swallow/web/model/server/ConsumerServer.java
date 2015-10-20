package com.dianping.swallow.web.model.server;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.util.NetUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:42:06
 */
public abstract class ConsumerServer extends Server {

	private int port;

	public ConsumerServer(String ip, int port) {
		this.setIp(ip);
		this.port = port;
		eventType = EventType.CONSUMER;
	}

	protected boolean isPortOpen() {
		int count = 0;
		boolean isOpened = false;
		do {
			if (count != 0) {
				threadSleep();
			}
			isOpened = NetUtil.isPortOpen(ip, port);
			count++;
		} while (!isOpened && count < 3);
		return isOpened;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "ConsumerServer [port=" + port + "] " + super.toString();
	}

}
