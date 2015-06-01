package com.dianping.swallow.common.internal.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;


/**
 * @author Leo Liang
 */
public class IPUtil {
   private IPUtil() {
   }

   /**
    * 获取第一个no loop address
    * 
    * @return first no loop address, or null if not exists
    */
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
         if (!address.isLoopbackAddress() && !address.isSiteLocalAddress() && !Inet6Address.class.isInstance(address)) {
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

   public static String getIpFromChannel(Channel channel) {
      if (channel == null) {
         return "unknown";
      }
      try {
         return getStrAddress(channel.getRemoteAddress());
      } catch (RuntimeException e) {
      }
      return "unknown";
   }
   
   public static String getStrAddress(SocketAddress address){
	   return address.toString().substring(1);
   }
   
   public static String getConnectionDesc(ChannelEvent e) {
		
		Channel channel = e.getChannel();
		String 	connectionDesc = IPUtil.getStrAddress(channel.getLocalAddress()) + 
				"->" + IPUtil.getStrAddress(channel.getRemoteAddress());
		return connectionDesc;
	}

   
   public static String getIp(String ipPort){

	   if(ipPort == null){
		   return ipPort;
	   }
	   
	   int index = ipPort.indexOf(":");
	   String result = ipPort;
	   if(index >= 0){
		   result = ipPort.substring(0, index);
	   }
	   return result;
   }

   
   
   public static String simpleLogIp(String ipPort) {
	   
	   if(ipPort == null){
		   return null;
	   }
	   ipPort = ipPort.trim();
	   int index = ipPort.indexOf(".");
	   if(index < 0){
		   return ipPort;
	   }
	   index = ipPort.indexOf(".", index + 1);
	   if(index < 0){
		   return ipPort;
	   }
	   return ipPort.substring(index + 1);
   }
   
}
