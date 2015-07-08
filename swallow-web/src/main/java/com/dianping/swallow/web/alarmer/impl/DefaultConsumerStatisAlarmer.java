package com.dianping.swallow.web.alarmer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.alarmer.ConsumerStatisAlarmer;
import com.dianping.swallow.web.monitor.AccumulationListener;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerServerStatisDataService;
import com.dianping.swallow.web.service.ConsumerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
*
* @author qiyin
*
*/
public class DefaultConsumerStatisAlarmer extends AbstractStatisAlarmer implements ConsumerStatisAlarmer , MonitorDataListener, AccumulationListener{

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerStatisAlarmer.class);
	
	@Autowired
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService; 
	
	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;
	
	@Autowired
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;
	
	@Autowired 
	private ConsumerServerStatisDataService consumerServerStatisDataService;
	
	@Autowired
	private ConsumerTopicStatisDataService  consumerTopicStatisDataService;
	
	@Autowired
	private ConsumerIdStatisDataService consumerIdStatisDataService;
	
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
