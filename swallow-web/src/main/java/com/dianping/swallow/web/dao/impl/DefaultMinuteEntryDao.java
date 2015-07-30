package com.dianping.swallow.web.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.MinuteEntryDao;
import com.dianping.swallow.web.model.dashboard.MinuteEntry;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年7月29日上午10:03:34
 */
@Component
public class DefaultMinuteEntryDao extends AbstractWriteDao implements MinuteEntryDao {

	private static final String MinuteEntry_COLLECTION = "swallowwebdashboardc";

	private static final String TIME = "time";

	@Override
	public int insert(MinuteEntry entry) {

		try {

			mongoTemplate.insert(entry, MinuteEntry_COLLECTION);
		} catch (Exception e) {

			logger.info(String.format("Error when save %s", entry));
			return ResponseStatus.MONGOWRITE.getStatus();
		}

		return ResponseStatus.SUCCESS.getStatus();
	}

	@Override
	public List<MinuteEntry> loadMinuteEntryPage(Date start, int limit) {

		Query query = new Query();
		Criteria criteria = Criteria.where(TIME).gte(start);
		query.addCriteria(criteria);
		
		query.limit(limit).with(new Sort(new Sort.Order(Direction.DESC, TIME)));

		List<MinuteEntry> minuteEntryList = mongoTemplate.find(query, MinuteEntry.class, MinuteEntry_COLLECTION);
		
		return minuteEntryList;
	}

}
