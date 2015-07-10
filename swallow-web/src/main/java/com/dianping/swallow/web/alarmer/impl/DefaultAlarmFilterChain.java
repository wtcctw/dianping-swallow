package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.List;

import com.dianping.swallow.web.alarmer.AlarmFilter;
import com.dianping.swallow.web.alarmer.AlarmFilterChain;

/**
*
* @author qiyin
*
*/
public class DefaultAlarmFilterChain implements AlarmFilterChain {

	private List<AlarmFilter> alarmFilters = new ArrayList<AlarmFilter>();

	public int index;

	@Override
	public boolean doNext() {
		if (index < alarmFilters.size()) {
			alarmFilters.get(index++).accept(this);
		}
		return false;
	}

	@Override
	public void registerFilter(AlarmFilter alarmFilter) {
		alarmFilters.add(alarmFilter);
	}

	@Override
	public void reset() {
		index = 0;
	}

}
