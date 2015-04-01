package com.dianping.swallow.common.internal.heartbeat;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 * 
 *         2015年3月31日 下午11:22:11
 */
public class DefaultHeartBeatReceiver implements HeartBeatReceiver, Runnable {

	private Map<Channel, Queue<HeartBeat>> heartBeats = new ConcurrentHashMap<Channel, Queue<HeartBeat>>();

	private final Logger                          logger                     = LoggerFactory.getLogger(getClass());
	
	private final int HEART_BEAT_SIZE = 10;
	
	private NoHeartBeatListener 	noHeartBeatListener;
	
	private ScheduledFuture<?>		future;

	public DefaultHeartBeatReceiver(ScheduledExecutorService scheduledThreadPool, NoHeartBeatListener noHeartBeatListener) {
		
		this.noHeartBeatListener = noHeartBeatListener;
		
		future = scheduledThreadPool.scheduleAtFixedRate(this, 0, HeartBeatSender.HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public void beat(Channel channel) {
		
		if(logger.isDebugEnabled()){
			logger.debug("[beat]" + channel);
		}
		
		Queue<HeartBeat> beats = heartBeats.get(channel);
		
		if(beats == null){
			beats = new ConcurrentLinkedQueue<HeartBeat>();
			heartBeats.put(channel, beats);
		}
		beats.offer(new HeartBeat(channel));
		if(beats.size() > HEART_BEAT_SIZE){
			beats.poll();
		}
	}

	@Override
	public void run() {

		for(Entry<Channel, Queue<HeartBeat>> channelBeats : heartBeats.entrySet()){
			Channel channel = channelBeats.getKey();
			Queue<HeartBeat> beats = channelBeats.getValue();

			HeartBeat heartBeat = beats.peek();
			if(heartBeat == null){
				logger.error("[run][no heartBeat]" + channel);
				continue;
			}
			
			Long current = System.currentTimeMillis();
			if((current - heartBeat.getHeartBeatTime()) > (MAX_HEARTBEAT_INTERVAL_MULTI * HeartBeatSender.HEART_BEAT_INTERVAL * 1000)){
				try{
					noHeartBeatListener.onNoHeartBeat(channel);
				}catch(Throwable th){
					logger.error("[run]", th);
				}
			}
		}
	}

	@Override
	public void cancelCheck() {
		future.cancel(false);
	}

	@Override
	public void remove(Channel channel) {
		
		heartBeats.remove(channel);
	}
}
