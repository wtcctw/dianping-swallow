package com.dianping.swallow.web.alarmer.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.alarmer.ConsumerStatisAlarmer;
import com.dianping.swallow.web.monitor.AccumulationListener;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;

/**
*
* @author qiyin
*
*/
public class DefaultConsumerStatisAlarmer extends AbstractStatisAlarmer implements ConsumerStatisAlarmer , MonitorDataListener, AccumulationListener{

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	@Autowired
	private AccumulationRetriever accumulationRetriever;
	
	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
		accumulationRetriever.registerListener(this);
	}

	@Override
	protected void doAlarm() {
		
	}
	
	public void doServerAlarm(){
		consumerDataRetriever.getServerQpx(QPX.SECOND);
	}
	
	public void doTopicAlarm(){
		
	}

	@Override
	public void achieveAccumulation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doConsumerIdAlarm() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void achieveMonitorData() {
		// TODO Auto-generated method stub
		
	}

	
}
