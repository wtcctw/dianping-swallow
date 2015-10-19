package com.dianping.swallow.web.dashboard;

import com.dianping.swallow.web.dashboard.model.MinuteEntry;
import com.dianping.swallow.web.service.MinuteEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:43
 */
@Component
public class DashboardContainer {

	public static final int TOTALENTRYSIZE = 10;

	public static final int FETCHENTRYSIZE = 10;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "minuteEntryService")
	private MinuteEntryService minuteEntryService;

	private Map<Date, MinuteEntry> dashboard = new LinkedHashMap<Date, MinuteEntry>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {

			return size() >= TOTALENTRYSIZE;
		}
	};
	
	public boolean insertMinuteEntry(MinuteEntry minuteEntry) {

		boolean result;

		synchronized (dashboard) {

			MinuteEntry me = dashboard.put(minuteEntry.getTime(), minuteEntry);
			result = me == null ? true : false;
			
			if(result){
				int status = minuteEntryService.insert(minuteEntry);

				Date date = minuteEntry.getTime();
				if (status == 0) {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Save MinuteEntry of time %tc to database successfully", date));
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Save MinuteEntry of time %tc to database failed", date));
					}
				}
			}
		}
		return result;
	}

	public List<MinuteEntry> loadMinuteEntries(Date start, Date stop) {
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Fetch MinuteEntries from %tc to %tc", start, stop));
		}
		Set<Date> treeSet = new TreeSet<Date>(new Comparator<Date>() {

			@Override
			public int compare(Date d1, Date d2) {
				int num = d2.compareTo(d1);
				return num;
			}
		});

		List<MinuteEntry> result = new ArrayList<MinuteEntry>();

		synchronized (dashboard) {
			Set<Date> dates = dashboard.keySet();
			for (Date dt : dates) {
				if (dt.after(start) && dt.before(stop)) {
					treeSet.add(dt);
				}
			}

			for (Date dt : treeSet) {
				if (result.size() >= FETCHENTRYSIZE) {
					break;
				}
				result.add(dashboard.get(dt));
			}
		}

		return result;
	}

}
