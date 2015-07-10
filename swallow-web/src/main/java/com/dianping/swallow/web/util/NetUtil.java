package com.dianping.swallow.web.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtil {

	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

	public static final String IP = getFirstNoLoopbackIP4Address();

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

	public static String getFirstNoLoopbackIP4Address() {
		Collection<String> allNoLoopbackIP4Addresses = getNoLoopbackIP4Addresses();
		if (allNoLoopbackIP4Addresses.isEmpty()) {
			return null;
		}
		return allNoLoopbackIP4Addresses.iterator().next();
	}

	public static Collection<String> getNoLoopbackIP4Addresses() {
		Collection<String> noLoopbackIP4Addresses = new ArrayList<String>();
		Collection<InetAddress> allInetAddresses = getAllHostAddress();

		for (InetAddress address : allInetAddresses) {
			if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()
					&& !Inet6Address.class.isInstance(address)) {
				noLoopbackIP4Addresses.add(address.getHostAddress());
			}
		}
		if (noLoopbackIP4Addresses.isEmpty()) {
			// 降低过滤标准，将site local address纳入结果
			for (InetAddress address : allInetAddresses) {
				if (!address.isLoopbackAddress() && !Inet6Address.class.isInstance(address)) {
					noLoopbackIP4Addresses.add(address.getHostAddress());
				}
			}
		}
		return noLoopbackIP4Addresses;
	}

	public static Collection<InetAddress> getAllHostAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			Collection<InetAddress> addresses = new ArrayList<InetAddress>();

			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					addresses.add(inetAddress);
				}
			}

			return addresses;
		} catch (SocketException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
