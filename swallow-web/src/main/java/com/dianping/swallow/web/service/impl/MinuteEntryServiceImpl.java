package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.MinuteEntryDao;
import com.dianping.swallow.web.model.dashboard.MinuteEntry;
import com.dianping.swallow.web.monitor.dashboard.DashboardContainer;
import com.dianping.swallow.web.service.MinuteEntryService;


/**
 * @author mingdongli
 *
 * 2015年7月29日上午11:51:02
 */
@Service("minuteEntryService")
public class MinuteEntryServiceImpl implements MinuteEntryService {
	
	private static final int ENTRY_SIZE = 10;
	
	@Autowired
	private MinuteEntryDao minuteEntryDao;
	
	@Autowired
	private DashboardContainer dashboardContainer;

	@Override
	public int insert(MinuteEntry entry) {
	
		return minuteEntryDao.insert(entry);
	}

	@Override
	public List<MinuteEntry> loadMinuteEntryPage(Date start) {

		List<MinuteEntry> entrys = dashboardContainer.loadMinuteEntries(start);
		int size = entrys.size();
		
		if(size < ENTRY_SIZE){
			List<MinuteEntry> entrys2 = new ArrayList<MinuteEntry>();
			
			if(size == 0){
				entrys2 =  minuteEntryDao.loadMinuteEntryPage(start, ENTRY_SIZE - size);
			}else{
				MinuteEntry minuteEntry = entrys.get(size - 1);
				entrys2 =  minuteEntryDao.loadMinuteEntryPage(minuteEntry.getTime(), ENTRY_SIZE - size);
			}
			
			entrys.addAll(entrys2);
		}
		
		return entrys;
	}

}
