package com.dianping.swallow.web.alarmer.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.alarmer.ConsumerStatisAlarmer;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;

/**
*
* @author qiyin
*
*/
public class DefaultConsumerStatisAlarmer extends AbstractStatisAlarmer implements ConsumerStatisAlarmer{

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Override
	protected void doAlarm() {
		
	}
	
	public void doServerQPSAlarm(){
		consumerDataRetriever.getServerQpx(QPX.SECOND);
	}
	
	public void doTopicQPSAlarm(){
		
	}
	
	public void doServerDelayAlarm(){
		consumerDataRetriever.getServerQpx(QPX.SECOND);
	}
	
	public void doTopicDelayAlarm(){
		
	}

	@Override
	public void doConsumerIdQPSAlarm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doConsumerIdDelayAlarm() {
		// TODO Auto-generated method stub
		
	}
}
