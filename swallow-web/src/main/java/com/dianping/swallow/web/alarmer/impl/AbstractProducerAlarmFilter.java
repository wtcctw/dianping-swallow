package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.alarmer.AlarmFilter;
import com.dianping.swallow.web.alarmer.AlarmFilterChain;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;

public abstract class AbstractProducerAlarmFilter implements AlarmFilter {

	@Override
	public boolean accept(AlarmFilterChain alarmFilterChain) {
		if(!doAccept()){
			return false;
		}else{
			return alarmFilterChain.doNext();
		}
	}
	
	public abstract boolean doAccept();
	

}
