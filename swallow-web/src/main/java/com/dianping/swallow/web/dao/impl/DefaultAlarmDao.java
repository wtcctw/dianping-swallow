package com.dianping.swallow.web.dao.impl;

import java.util.Date;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.AlarmDao;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.mongodb.WriteResult;

/**
 * 
 * @author qiyin
 *
 */
@Service("alarmDao")
public class DefaultAlarmDao extends AbstractWriteDao implements AlarmDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAlarmDao.class);

	private static final String ALARM_COLLECTION = "swallowwebalarmtestc";

	private static final String RECEIVER_FIELD = "receiver";

	private static final String CREATETIME_FIELD = "createTime";

	private static final String EVENTID_FIELD = "eventId";

	private static final String ID_FIELD = "id";

	private static final String RELATED_FIELD = "related";

	private static final String SUBRELATED_FIELD = "subRelated";

	@Override
	public boolean insert(Alarm alarm) {
		try {
			mongoTemplate.save(alarm, ALARM_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save alarm " + alarm, e);
		}
		return false;
	}

	@Override
	public boolean update(Alarm alarm) {
		return insert(alarm);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, Alarm.class, ALARM_COLLECTION);
		return result.getN();
	}

	@Override
	public Alarm findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		Alarm alarm = mongoTemplate.findOne(query, Alarm.class, ALARM_COLLECTION);
		return alarm;
	}

	@Override
	public Pair<List<Alarm>, Long> findByPage(String receiver, String related, Date startTime, Date endTime,
			int offset, int limit) {
		if (endTime == null) {
			endTime = new Date();
		}
		Criteria criteria = null;
		if (StringUtils.isNotBlank(receiver)) {
			criteria = Criteria.where(RECEIVER_FIELD).is(receiver);
		}
		if (StringUtils.isNotBlank(related)) {
			if (criteria != null) {
				criteria = criteria.and(RELATED_FIELD).is(related);
			} else {
				criteria = Criteria.where(RELATED_FIELD).is(related);
			}
		}
		if (startTime != null && endTime != null) {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).gte(startTime).lte(endTime);
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).gte(startTime).lte(endTime);
			}
		} else {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).lte(endTime);
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).lte(endTime);
			}
		}
		Query query = new Query(criteria);
		Query queryCount = new Query(criteria);
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, EVENTID_FIELD)));
		List<Alarm> alarms = mongoTemplate.find(query, Alarm.class, ALARM_COLLECTION);
		long count = mongoTemplate.count(queryCount, ALARM_COLLECTION);
		Pair<List<Alarm>, Long> result = new Pair<List<Alarm>, Long>();
		result.setFirst(alarms);
		result.setSecond(count);
		return result;
	}

	@Override
	public Pair<List<Alarm>, Long> findByPage(String receiver, String related, String subRelated, Date startTime,
			Date endTime, int offset, int limit) {
		if (endTime == null) {
			endTime = new Date();
		}
		Criteria criteria = null;
		if (StringUtils.isNotBlank(receiver)) {
			criteria = Criteria.where(RECEIVER_FIELD).is(receiver);
		}
		if (StringUtils.isNotBlank(related)) {
			if (criteria != null) {
				criteria = criteria.and(RELATED_FIELD).is(related);
			} else {
				criteria = Criteria.where(RELATED_FIELD).is(related);
			}
		}
		if (StringUtils.isNotBlank(subRelated)) {
			if (criteria != null) {
				criteria = criteria.and(SUBRELATED_FIELD).is(subRelated);
			} else {
				criteria = Criteria.where(SUBRELATED_FIELD).is(subRelated);
			}
		}
		if (startTime != null && endTime != null) {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).gte(startTime).lte(endTime);
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).gte(startTime).lte(endTime);
			}
		} else {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).lte(endTime);
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).lte(endTime);
			}
		}
		Query query = new Query(criteria);
		Query queryCount = new Query(criteria);
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, EVENTID_FIELD)));
		List<Alarm> alarms = mongoTemplate.find(query, Alarm.class, ALARM_COLLECTION);
		long count = mongoTemplate.count(queryCount, ALARM_COLLECTION);
		Pair<List<Alarm>, Long> result = new Pair<List<Alarm>, Long>();
		result.setFirst(alarms);
		result.setSecond(count);
		return result;
	}

	@Override
	public Alarm findByEventId(long eventId) {
		Query query = new Query(Criteria.where(EVENTID_FIELD).is(eventId));
		Alarm alarm = mongoTemplate.findOne(query, Alarm.class, ALARM_COLLECTION);
		return alarm;
	}
}
