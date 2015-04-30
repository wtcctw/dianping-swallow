package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessageUtil;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.AbstractTotalable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfoTotalMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年4月26日 上午9:48:42
 */
public class ConsumerIdData extends AbstractTotalable implements KeyMergeable, TotalBuilder{
	
	@Transient
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	private MessageInfoTotalMap  sendMessages  = new MessageInfoTotalMap();
	private MessageInfoTotalMap  ackMessages  = new MessageInfoTotalMap();
	
	@Transient
	@JsonIgnore
	private Map<Long, Long>  messageSendTimes = new ConcurrentHashMap<Long, Long>();
	
	public ConsumerIdData(){
					
	}
	
	@Transient
	@JsonIgnore
	public MessageInfo getTotalSendMessages(){
		return sendMessages.getTotal();
	}

	@Transient
	@JsonIgnore
	public MessageInfo getTotalAckMessages(){
		return ackMessages.getTotal();
			
	}

	@Override
	public void buildTotal() {
		sendMessages.buildTotal();
		ackMessages.buildTotal();
	}

	@Override
	public Object getTotal() {
		return null;
	}

	@Override
	public void setTotal() {
		super.setTotal();
		sendMessages.setTotal();
		ackMessages.setTotal();
		
	}

	public void merge(Mergeable merge) {
		
		checkType(merge);
		
		ConsumerIdData toMerge = (ConsumerIdData) merge;
		
		
		sendMessages.merge(toMerge.sendMessages);
		ackMessages.merge(toMerge.ackMessages);
	}
	
	private void checkType(Mergeable merge) {
		if(!(merge instanceof ConsumerIdData)){
			throw new IllegalArgumentException("wrong type " + merge.getClass());
		}
	}

	@Override
	public void merge(String key, KeyMergeable merge) {
		
		checkType(merge);
		
		ConsumerIdData toMerge = (ConsumerIdData) merge;
		sendMessages.merge(key, toMerge.sendMessages);
		ackMessages.merge(key, toMerge.ackMessages);
	}



	public void sendMessage(String consumerIp, SwallowMessage message){
		
		//记录消息发送时间
		messageSendTimes.put(message.getMessageId(), System.currentTimeMillis());
		
		MessageInfo messageInfo = MapUtil.getOrCreate(sendMessages, consumerIp, MessageInfo.class);
		
		long saveTime = SwallowMessageUtil.getSaveTime(message);
		if(saveTime <= 0){
			saveTime = System.currentTimeMillis();
		}
		messageInfo.addMessage(message.getMessageId(), saveTime, System.currentTimeMillis());
		
	}
	
	public void ackMessage(String consumerIp, SwallowMessage message){
		
		Long messageId = message.getMessageId();
		Long sendTime = messageSendTimes.get(messageId);
		
		if(sendTime == null){
			logger.warn("[ackMessage][unfound message]" + messageId + "," + this);
			sendTime = System.currentTimeMillis();
		}
		
		try{
			MessageInfo messageInfo = MapUtil.getOrCreate(ackMessages, consumerIp, MessageInfo.class);
			messageInfo.addMessage(messageId, sendTime, System.currentTimeMillis());
			
		}finally{
			messageSendTimes.remove(messageId);
		}
	}

	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof ConsumerIdData)){
			return false;
		}
		
		ConsumerIdData cmp = (ConsumerIdData) obj;
		return cmp.sendMessages.equals(sendMessages) 
				&& cmp.ackMessages.equals(ackMessages);
	}
	
	@Override
	public String toString() {
		return JsonBinder.getNonEmptyBinder().toJson(this);
	}

	@Override
	public int hashCode() {
		
		return (int) (sendMessages.hashCode() ^ ackMessages.hashCode());
	}


}

