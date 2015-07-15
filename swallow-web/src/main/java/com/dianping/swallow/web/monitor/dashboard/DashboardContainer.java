package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;


/**
 * @author mingdongli
 *
 * 2015年7月7日上午9:36:43
 */
@Component
public class DashboardContainer {

	public static final int TOTALENTRYSIZE = 70;

	private Map<String, List<MinuteEntry>> dashboards = new  ConcurrentHashMap<String, List<MinuteEntry>>();
	
	private AtomicInteger entrySize = new AtomicInteger(0);
	
	public boolean insertMinuteEntry(String key, MinuteEntry minuteEntry) {
		List<MinuteEntry> minuteEntries = null;
		boolean result;

		if (dashboards.containsKey(key)) {
			minuteEntries = dashboards.get(key);
		} else {
			minuteEntries = new ArrayList<MinuteEntry>();
			dashboards.put(key, minuteEntries);
		}
		synchronized (minuteEntries) {
			int size = minuteEntries.size();

			while (size >= TOTALENTRYSIZE) {
				minuteEntries.remove(0);
				size--;
			}
			result = minuteEntries.add(minuteEntry);
			entrySize.set(minuteEntries.size());
		}
		return result;
	}

	public List<MinuteEntry> fetchMinuteEntries(String key, int offset, int size) {
		if (!dashboards.containsKey(key)) {
			return new ArrayList<MinuteEntry>();
		}

		List<MinuteEntry> result = new ArrayList<MinuteEntry>();
		List<MinuteEntry> minuteEntries = dashboards.get(key);

		synchronized (minuteEntries) {
			int actualSize = minuteEntries.size();

			if (size > actualSize) {
				size = actualSize;
			}
			for (int i = actualSize - size - offset; i < actualSize - offset; i++) {
				result.add(minuteEntries.get(i));
			}
			entrySize.set(minuteEntries.size());
		}
		Collections.reverse(result);
		return result;
	}

	public Map<String, List<MinuteEntry>> getDashboards() {
		return dashboards;
	}

	public void setDashboards(Map<String, List<MinuteEntry>> dashboards) {
		this.dashboards = dashboards;
	}

	public int getEntrySize() {
		return entrySize.get();
	}

}

