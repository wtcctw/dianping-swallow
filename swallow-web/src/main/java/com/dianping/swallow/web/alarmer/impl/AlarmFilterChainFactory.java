package com.dianping.swallow.web.alarmer.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.alarmer.AlarmFilterChain;

@Component
public class AlarmFilterChainFactory implements InitializingBean {

	public static AlarmFilterChainFactory chainFactory;

	public AlarmFilterChain createFilterChain(){
		AlarmFilterChain alarmFilterChain = new DefaultAlarmFilterChain();
		return alarmFilterChain;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		chainFactory = this;
	}

}
