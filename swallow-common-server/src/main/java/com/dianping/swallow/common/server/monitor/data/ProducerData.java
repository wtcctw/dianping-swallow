package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年4月26日 上午9:42:55
 */
public class ProducerData extends TotalMap<MessageInfo>{
	
	private static final long serialVersionUID = 1L;
			
	public void sendMessage(String producerIp, long messageId, long sendTime, long saveTime){
		
		MessageInfo messageInfo = MapUtil.getOrCreate(this, producerIp, MessageInfo.class);
		messageInfo.addMessage(messageId, sendTime, saveTime);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof ProducerData)){
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return hashCode();
	}

	@Override
	protected MessageInfo createValue() {
		return new MessageInfo();
	}
}

