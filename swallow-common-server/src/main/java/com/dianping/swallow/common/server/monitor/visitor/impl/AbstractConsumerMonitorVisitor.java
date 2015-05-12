package com.dianping.swallow.common.server.monitor.visitor.impl;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;
import com.dianping.swallow.common.server.monitor.visitor.ConsumerMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.QPX;


/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:18:40
 */
public abstract class AbstractConsumerMonitorVisitor extends AbstractMonitorVisitor implements ConsumerMonitorVisitor{
	
	protected List<MessageInfo> sendRawData = new LinkedList<MessageInfo>();

	protected List<MessageInfo> ackRawData = new LinkedList<MessageInfo>();

	public AbstractConsumerMonitorVisitor(String topic) {
		super(topic);
	}

	@Override
	public List<Long> buildSendDelay(int intervalTimeSeconds){
		
		return buildDelay(intervalTimeSeconds, sendRawData);
	}

	@Override
	public List<Long> buildAckDelay(int intervalTimeSeconds){

		return buildDelay(intervalTimeSeconds, ackRawData);

	}
	
	public List<Long> buildSendQpx(int intervalTimeSeconds, QPX qpx){
		
		return buildQpx(sendRawData, intervalTimeSeconds, qpx);
	}



	public List<Long> buildAckQpx(int intervalTimeSeconds, QPX qpx){
		
		return buildQpx(ackRawData, intervalTimeSeconds, qpx);
	}


}
