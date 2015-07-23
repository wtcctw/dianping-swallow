package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.dashboard.MinuteEntry;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:43
 */
@Component
public class DashboardContainer {

	public static final int TOTALENTRYSIZE = 70;

	public static final int FETCHENTRYSIZE = 10;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<Date, MinuteEntry> dashboards = new LinkedHashMap<Date, MinuteEntry>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {
			return size() >= TOTALENTRYSIZE;
		}
	};

	public boolean insertMinuteEntry(MinuteEntry minuteEntry) {

		boolean result;

		synchronized (dashboards) {

			MinuteEntry me = dashboards.put(minuteEntry.getTime(), minuteEntry);
			result = me == null ? true : false;
		}
		return result;
	}

	public List<MinuteEntry> fetchMinuteEntries(Date stop) {

		Calendar calendarstart = Calendar.getInstance();
		calendarstart.setTime(stop);
		calendarstart.add(Calendar.MINUTE, -11);
		calendarstart.clear(Calendar.SECOND);
		calendarstart.clear(Calendar.MILLISECOND);
		Date start = calendarstart.getTime();
		
		logger.info(String.format("Fetch MinuteEntries from %tc to %tc", start, stop));
		Set<Date> treeSet = new TreeSet<Date>(new Comparator<Date>() {

			@Override
			public int compare(Date d1, Date d2) {
				int num = d2.compareTo(d1);
				return num;
			}
		});

		List<MinuteEntry> result = new ArrayList<MinuteEntry>();

		synchronized (dashboards) {
			Set<Date> dates = dashboards.keySet();
			for (Date dt : dates) {
				if (dt.after(start) && dt.before(stop)) {
					treeSet.add(dt);
				}
			}

			for (Date dt : treeSet) {
				if(result.size() >= FETCHENTRYSIZE){
					break;
				}
				result.add(dashboards.get(dt));
			}
		}

		return result;
	}

	public Map<Date, MinuteEntry> getDashboards() {
		return dashboards;
	}

	public void setDashboards(Map<Date, MinuteEntry> dashboards) {
		this.dashboards = dashboards;
	}


}
