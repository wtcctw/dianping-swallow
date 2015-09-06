package com.dianping.swallow.common.internal.heartbeat;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dianping.swallow.common.internal.packet.PacketType;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;

/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午9:50:59
 */
public class DefaultHeartBeatSender implements HeartBeatSender{
	
	private Logger logger = Logger.getLogger(getClass());

	private ConcurrentHashMap<Channel, ScheduledFuture<?>>  channels = new ConcurrentHashMap<Channel, ScheduledFuture<?>>();
	
	private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.getCpuCount(), new MQThreadFactory("HEART-BEAT-SENDER"));
	
	@Override
	public void addChannel(Channel channel) {

		if(logger.isInfoEnabled()){
			logger.info("[addChannel]" + channel);
		}
		
		ScheduledFuture<?> future = doSchedule(scheduled, new HeartBeatTask(channel));
		
		channels.put(channel, future);
		
	}

	protected ScheduledFuture<?> doSchedule(ScheduledExecutorService scheduled, Runnable task) {
		
		 return scheduled.scheduleAtFixedRate(task , HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public void removeChannel(Channel channel) {

		if(logger.isInfoEnabled()){
			logger.info("[removeChannel]" + channel);
		}

		ScheduledFuture<?> future = channels.remove(channel);
		if(future != null){
			future.cancel(false);
		}else{
			if(logger.isInfoEnabled()){
				logger.info("[removeChannel][already cancelled]" + channel);
			}
		}
	}
	
	
	class HeartBeatTask implements Runnable{

		private Channel channel;
		
		public HeartBeatTask(Channel channel){
			this.channel = channel;
		}
		
		@Override
		public void run() {
			try{
				if(logger.isDebugEnabled()){
					logger.debug("[run][heartbeat]" + channel);
				}
				PktConsumerMessage message = new PktConsumerMessage();
				message.setPacketType(PacketType.HEART_BEAT);
				channel.writeAndFlush(message);
			}catch(Throwable th){
				logger.error("[run][error heart beat]" + channel, th);
			}
		}
		
	}

}
