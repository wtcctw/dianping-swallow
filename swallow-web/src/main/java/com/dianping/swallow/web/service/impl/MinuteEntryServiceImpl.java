package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.MinuteEntryDao;
import com.dianping.swallow.web.dashboard.DashboardContainer;
import com.dianping.swallow.web.dashboard.model.DashboardEnum;
import com.dianping.swallow.web.dashboard.model.Entry;
import com.dianping.swallow.web.dashboard.model.MinuteEntry;
import com.dianping.swallow.web.dashboard.model.ResultEntry;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MinuteEntryService;

/**
 * @author mingdongli
 *
 *         2015年7月29日上午11:51:02
 */
@Service("minuteEntryService")
public class MinuteEntryServiceImpl extends AbstractSwallowService implements MinuteEntryService {

	private static final int ENTRY_SIZE = 10;

	@Autowired
	private MinuteEntryDao minuteEntryDao;

	@Autowired
	private DashboardContainer dashboardContainer;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@Resource(name = "ipDescManager")
	private IPDescManager ipDescManager;

	@Override
	public int insert(MinuteEntry entry) {

		return minuteEntryDao.insert(entry);
	}

	@Override
	public List<ResultEntry> loadMinuteEntryPage(Date start, Date stop, String type) {

		List<MinuteEntry> entrys = dashboardContainer.loadMinuteEntries(start, stop);

		int size = entrys.size();
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Load MinuteEntries from memory of %d record", size));
		}

		if (size < ENTRY_SIZE) {
			List<MinuteEntry> entrys2 = new ArrayList<MinuteEntry>();

			if (size == 0) {
				entrys2 = minuteEntryDao.loadMinuteEntryPage(start, stop, ENTRY_SIZE - size);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Load MinuteEntries from database of %d record from %tc to %tc",
							entrys2.size(), start, stop));
				}
			} else {
				MinuteEntry minuteEntry = entrys.get(size - 1);
				Date stop2 = minuteEntry.getTime();
				entrys2 = minuteEntryDao.loadMinuteEntryPage(start, stop2, ENTRY_SIZE - size);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Load MinuteEntries from database of %d record from %tc to %tc",
							entrys2.size(), start, stop2));
				}
			}

			entrys.addAll(entrys2);
		}

		List<ResultEntry> entryList = new ArrayList<ResultEntry>();
		for (MinuteEntry me : entrys) {
			DashboardEnum dashboardEnum = DashboardEnum.findByType(type);
			List<com.dianping.swallow.web.dashboard.model.Entry> list = me.getListByType(dashboardEnum);
			for (Entry e : list) {
				if (StringUtils.isBlank(e.getDpMobile()) || StringUtils.isBlank(e.getEmail())
						|| StringUtils.isBlank(e.getName())){
					setCmdpInfo(e);
				}
			}
			ResultEntry re = new ResultEntry();
			re.setResult(list);
			re.setTime(me.getTime());
			entryList.add(re);
		}
		return entryList;
	}

	private void setCmdpInfo(Entry entry) {

		String topic = entry.getTopic();
		String consumerid = entry.getConsumerId();

		Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL, topic,
				consumerid);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Load ips %s of topic %s and consumerid %s", ips.toString(), topic, consumerid));
		}

		String ip = loadFirstElement(ips);
		IPDesc iPDesc = ipDescManager.getIPDesc(ip);
		String mobile = "Blank";
		String email = "Blank";
		String name = "Blank";
		if (iPDesc != null) {
			mobile = iPDesc.getDpMobile();
			email = iPDesc.getEmail();
			name = iPDesc.getName();
		}

		entry.setEmail(email).setName(name).setDpMobile(mobile);
	}

	private static String loadFirstElement(Set<String> set) {

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			return it.next();
		}
		return "";
	}

}
