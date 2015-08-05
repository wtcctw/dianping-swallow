package com.dianping.swallow.web.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author qiyin
 *
 * 2015年8月1日 下午11:20:20
 */
public class NetUtil {

	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

	public static final String IP = getFirstNoLoopbackIP4Address();

	private static final int TIME_OUT = 300;

	public static boolean isPortOpen(String host, int port) {
		Socket socket = null;
		try {
			InetAddress inetAddr = InetAddress.getByName(host);
			socket = new Socket();
			socket.connect(new InetSocketAddress(inetAddr, port), TIME_OUT);
			return true;
		} catch (IOException e) {
			return false;
		}
		finally {
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
