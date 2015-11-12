package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.AlarmDao;
import com.dianping.swallow.web.model.alarm.Alarm;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
@Service("alarmDao")
public class DefaultAlarmDao extends AbstractStatsDao implements AlarmDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAlarmDao.class);

	private static final String ALARM_COLLECTION = "ALARM";

	private static final String RECEIVER_FIELD = "sendInfos.receiver";

	private static final String CREATETIME_FIELD = "createTime";

	private static final String EVENTID_FIELD = "eventId";

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
	public Pair<List<Alarm>, Long> findByPage(AlarmParam alarmParam) {
		if (alarmParam.getEndTime() == null) {
			alarmParam.setEndTime(new Date());
		}
		Criteria criteria = null;
		if (StringUtils.isNotBlank(alarmParam.getReceiver())) {
			criteria = Criteria.where(RECEIVER_FIELD).is(alarmParam.getReceiver());
		}
		if (StringUtils.isNotBlank(alarmParam.getRelated())) {
			if (criteria != null) {
				criteria = criteria.and(RELATED_FIELD).is(alarmParam.getRelated());
			} else {
				criteria = Criteria.where(RELATED_FIELD).is(alarmParam.getRelated());
			}
		}
		if (StringUtils.isNotBlank(alarmParam.getSubRelated())) {
			if (criteria != null) {
				criteria = criteria.and(SUBRELATED_FIELD).is(alarmParam.getSubRelated());
			} else {
				criteria = Criteria.where(SUBRELATED_FIELD).is(alarmParam.getSubRelated());
			}
		}
		if (alarmParam.getStartTime() != null && alarmParam.getEndTime() != null) {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).gte(alarmParam.getStartTime()).lte(alarmParam.getEndTime());
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).gte(alarmParam.getStartTime()).lte(alarmParam.getEndTime());
			}
		} else {
			if (criteria != null) {
				criteria = criteria.and(CREATETIME_FIELD).lte(alarmParam.getEndTime());
			} else {
				criteria = Criteria.where(CREATETIME_FIELD).lte(alarmParam.getEndTime());
			}
		}
		Query query = new Query(criteria);
		Query queryCount = new Query(criteria);
		query.skip(alarmParam.getOffset()).limit(alarmParam.getLimit()).with(new Sort(new Sort.Order(Direction.DESC, EVENTID_FIELD)));
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
