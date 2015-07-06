package com.dianping.swallow.web.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtil {

	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

	public static boolean isPortUsing(String host, int port) {
		Socket socket = null;
		try {
			InetAddress inetAddr = InetAddress.getByName(host);
			socket = new Socket(inetAddr, port);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (socket != null && socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("[isPortUsing] socket close failed.", e);
				}
			}
			socket = null;
		}
	}

}
