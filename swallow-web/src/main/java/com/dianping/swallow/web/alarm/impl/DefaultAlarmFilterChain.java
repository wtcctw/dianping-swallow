package com.dianping.swallow.web.alarm.impl;

import java.util.ArrayList;
import java.util.List;

import com.dianping.swallow.web.alarm.AlarmFilter;
import com.dianping.swallow.web.alarm.AlarmFilterChain;

/**
*
* @author qiyin
*
*/
public class DefaultAlarmFilterChain implements AlarmFilterChain {

	private List<AlarmFilter> alarmFilters = new ArrayList<AlarmFilter>();

	private int index;
	
	private String chainName;

	@Override
	public boolean doNext() {
		if (index < alarmFilters.size()) {
			return alarmFilters.get(index++).accept(this);
		}
		return true;
	}

	@Override
	public void registerFilter(AlarmFilter alarmFilter) {
		alarmFilters.add(alarmFilter);
	}

	@Override
	public void reset() {
		index = 0;
	}

	@Override
	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	@Override
	public String getChainName() {
		return this.chainName;
	}

}
