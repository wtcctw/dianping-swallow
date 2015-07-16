package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.DataMonitorController;


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

			int restSize = actualSize - offset;
			if(offset >= actualSize){
				return result;
			}else if (restSize < DataMonitorController.ENTRYSIZE) {
				result = minuteEntries.subList(0, restSize);
			}else{
				for (int i = restSize - size ; i < restSize; i++) {
					result.add(minuteEntries.get(i));
				}
			}
		}
		return reverseList(result);
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
	
	private List<MinuteEntry> reverseList(List<MinuteEntry> entry){
		
		List<MinuteEntry> result = new ArrayList<MinuteEntry>();
		int size = entry.size();
		for(int i = size - 1; i >= 0; i--){
			result.add(entry.get(i));
		}
		return result;
	}

}

