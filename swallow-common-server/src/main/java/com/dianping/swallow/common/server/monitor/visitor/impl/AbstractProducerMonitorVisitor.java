package com.dianping.swallow.common.server.monitor.visitor.impl;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;
import com.dianping.swallow.common.server.monitor.visitor.ProducerMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.QPX;


/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:18:40
 */
public abstract class AbstractProducerMonitorVisitor extends AbstractMonitorVisitor implements ProducerMonitorVisitor{

	protected List<MessageInfo>  allRawData = new LinkedList<MessageInfo>(); 
	

	public AbstractProducerMonitorVisitor(String topic) {
		super(topic);
	}
	
	protected List<MessageInfo> getRawData(){
		
		return allRawData;
	}

	@Override
	public List<Long> buildSaveDelay(int intervalTimeSeconds){
		
		return buildDelay(intervalTimeSeconds, allRawData);
	}

	public List<Long> buildSaveQpx(int intervalTimeSeconds, QPX qpx){
		
		return null;
	}

}
