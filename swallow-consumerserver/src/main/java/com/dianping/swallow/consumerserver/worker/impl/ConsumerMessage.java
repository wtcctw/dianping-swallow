package com.dianping.swallow.consumerserver.worker.impl;

import io.netty.channel.Channel;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.worker.SendAckManager;

/**
 * @author mengwenchao
 *
 * 2015年11月12日 下午5:43:40
 */
public class ConsumerMessage {

	private SwallowMessage message;

	private long gmt;
	
	private Channel channel;
	
	private SendAckManager sendAckManager;

	public ConsumerMessage(SwallowMessage message, SendAckManager sendAckManager) {
		this.message = message;
		this.gmt = System.currentTimeMillis();
		this.sendAckManager = sendAckManager;
	}
	
	@Override
	public String toString() {
		return "ConsumerMessage [message=" + messageIdDesc() + ", gmt=" + gmt + ",channel:" + channel +"]";
	}

	private String messageIdDesc() {
		
		if(message.getBackupMessageId() != null){
			return "backupid:" + message.getBackupMessageId();
		}
		
		return "mid:" + message.getMessageId();
	}

	public Long getAckId() {
		return message.getMessageId();
	}
	
	public  long getGmt(){
		return gmt;
	}
	
	public SwallowMessage getMessage(){
		return message;
	}

	public void beginSend(Channel channel){
		this.channel = channel;
	}
	
	public Channel getChannel(){
		return this.channel;
	}

	public void exceptionWhileSend(Throwable th){
		this.sendAckManager.exceptionWhileSending(this, th);
	}
}
