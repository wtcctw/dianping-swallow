package com.dianping.swallow.consumer.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ConsumerThread的作用是，它会不断的保持与ConsumerServer的连接(一个channel关闭后继续建立新的channel)<br>
 * 使用时，一个Consumer拥有master和slave2个线程，2个线程不断交替尝试连接master和slave服务器，直到其中一个连接成功。<br>
 * 当master和slave线程其中一个连接成功后，另外一个线程将阻塞<br>
 * 已经连接成功的线程，当连接被关闭后，会睡眠一会，然后继续进入2个线程交替尝试的场景。<br>
 * 
 * @author wukezhu
 */
public class ConsumerThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

	private Bootstrap bootstrap;

	private volatile InetSocketAddress remoteAddress;
	private volatile boolean remoteAddressChanged = false;

	private long interval;

	private ConsumerImpl consumerImpl;
	
	private Channel channel;
	
	public ConsumerThread(ConsumerImpl consumerImpl) {

		this.consumerImpl = consumerImpl;
	}

	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public void setRemoteAddress(InetSocketAddress remoteAddress) {

		if(this.remoteAddress == null){
			this.remoteAddress = remoteAddress;
			return;
		}

		if(this.remoteAddress.equals(remoteAddress)){
			logger.warn("[setRemoteAddress][address equal]" + this.remoteAddress + "," + remoteAddress);
			return;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[setRemoteAddress][address changed]" + this.remoteAddress + "->" + remoteAddress);
		}
		this.remoteAddress = remoteAddress;
		remoteAddressChanged = true;
		disConnect();
		
		
	}

	private void disConnect() {
		
		if(channel != null && channel.isActive()){
			if(logger.isInfoEnabled()){
				logger.info("[disConnect]" + channel);
			}
			channel.close();
		}
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public void run() {

		ChannelFuture future = null;
		while (!Thread.currentThread().isInterrupted()) {
			synchronized (bootstrap) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("[run][connecting][" + getDesc() + "]" + remoteAddress);
					}
					future = bootstrap.connect(remoteAddress);
					future.await();
					if (logger.isDebugEnabled()) {
						logger.debug("[run][await finished][" + getDesc() + "]" + remoteAddress);
					}
					
					channel = future.channel();
					if (channel.isActive()) {

						SocketAddress localAddress = channel.localAddress();
						if (logger.isInfoEnabled()) {
							logger.info(
									"[run][connected][" + getDesc() + "]" + localAddress + "->" + remoteAddress);
						}

						if(!channel.remoteAddress().equals(remoteAddress)){
							if(logger.isInfoEnabled()){
								logger.info("[run][remote address not equal]" + channel.remoteAddress() + "," + remoteAddress);
							}
							disConnect();
						}
						
						channel.closeFuture().await();// 等待channel关闭，否则一直阻塞！
						if (logger.isInfoEnabled()) {
							logger.info(
									"[run][closed   ][" + getDesc() + "]" + localAddress + "->" + remoteAddress);
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (RuntimeException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			sleepIfNecessary(interval);
		}

		if (future != null && future.channel() != null) {
			future.channel().close();// 线程被中断了，主动关闭连接
		}

		if (logger.isInfoEnabled()) {
			logger.info("ConsumerThread(remoteAddress=" + remoteAddress + ") done.");
		}
	}

	private void sleepIfNecessary(long interval) {
		
		if(remoteAddressChanged){
			remoteAddressChanged = false;
			return;
		}

		try {
			sleep(interval);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private String getDesc() {
		return consumerImpl.toString() + "@" + toString();
	}
}
