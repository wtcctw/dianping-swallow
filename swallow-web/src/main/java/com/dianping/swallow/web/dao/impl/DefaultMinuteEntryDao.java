package com.dianping.swallow.web.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.MinuteEntryDao;
import com.dianping.swallow.web.dashboard.model.MinuteEntry;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年7月29日上午10:03:34
 */
@Component
public class DefaultMinuteEntryDao extends AbstractWriteDao implements MinuteEntryDao {

	private static final String MINUTEENTRY_COLLECTION = "MINUTE_ENTRY";

	private static final String TIME = "time";

	@Override
	public int insert(MinuteEntry entry) {

		try {

			mongoTemplate.insert(entry, MINUTEENTRY_COLLECTION);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("Error when save entry.", e);
			}
			return ResponseStatus.MONGOWRITE.getStatus();
		}

		return ResponseStatus.SUCCESS.getStatus();
	}

	@Override
	public List<MinuteEntry> loadMinuteEntryPage(Date start, Date stop, int limit) {

		Query query = new Query();
		Criteria criteria = Criteria.where(TIME).lt(stop).gt(start);
		query.addCriteria(criteria);
		
		query.limit(limit).with(new Sort(new Sort.Order(Direction.DESC, TIME)));

		List<MinuteEntry> minuteEntryList = mongoTemplate.find(query, MinuteEntry.class, MINUTEENTRY_COLLECTION);
		
		return minuteEntryList;
	}

}
