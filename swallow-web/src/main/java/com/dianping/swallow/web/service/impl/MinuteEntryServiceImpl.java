package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.MinuteEntryDao;
import com.dianping.swallow.web.dashboard.DashboardContainer;
import com.dianping.swallow.web.dashboard.model.MinuteEntry;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MinuteEntryService;


/**
 * @author mingdongli
 *
 * 2015年7月29日上午11:51:02
 */
@Service("minuteEntryService")
public class MinuteEntryServiceImpl extends AbstractSwallowService implements MinuteEntryService {
	
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
	public List<MinuteEntry> loadMinuteEntryPage(Date start, Date stop) {

		List<MinuteEntry> entrys = dashboardContainer.loadMinuteEntries(start, stop);
		
		int size = entrys.size();
		if(logger.isInfoEnabled()){
			logger.info(String.format("Load MinuteEntries from memory of %d record", size));
		}
		
		if(size < ENTRY_SIZE){
			List<MinuteEntry> entrys2 = new ArrayList<MinuteEntry>();
			
			if(size == 0){
				entrys2 =  minuteEntryDao.loadMinuteEntryPage(start, stop, ENTRY_SIZE - size);
				if(logger.isInfoEnabled()){
					logger.info(String.format("Load MinuteEntries from database of %d record from %tc to %tc", entrys2.size(), start, stop));
				}
			}else{
				MinuteEntry minuteEntry = entrys.get(size - 1);
				Date stop2 = minuteEntry.getTime();
				entrys2 =  minuteEntryDao.loadMinuteEntryPage(start, stop2, ENTRY_SIZE - size);
				if(logger.isInfoEnabled()){
					logger.info(String.format("Load MinuteEntries from database of %d record from %tc to %tc", entrys2.size(), start, stop2));
				}
			}
			
			entrys.addAll(entrys2);
		}
		
		return entrys;
	}

}
