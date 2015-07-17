package com.dianping.swallow.web.dao.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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

	private static final String ALARM_COLLECTION = "swallowwebalarmc";

	private static final String RECEIVER_FIELD = "receiver";

	private static final String CREATETIME_FIELD = "createTime";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(Alarm alarm) {
		try {
			mongoTemplate.save(alarm, ALARM_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + alarm, e);
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
	public List<Alarm> findByReceiver(String receiver, int offset, int limit) {
		Query query = new Query(Criteria.where(RECEIVER_FIELD).is(receiver));
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, CREATETIME_FIELD)));
		List<Alarm> alarms = mongoTemplate.find(query, Alarm.class, ALARM_COLLECTION);
		return alarms;
	}

	@Override
	public List<Alarm> findByCreateTime(Date createTime, int offset, int limit) {
		Query query = new Query(Criteria.where(CREATETIME_FIELD).gte(createTime));
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, CREATETIME_FIELD)));
		List<Alarm> alarms = mongoTemplate.find(query, Alarm.class, ALARM_COLLECTION);
		return alarms;
	}

	@Override
	public long countByCreateTime(Date createTime) {
		Query query = new Query(Criteria.where(CREATETIME_FIELD).gte(createTime));
		return mongoTemplate.count(query, ALARM_COLLECTION);
	}
	
	@Override
	public long countByReceiver(String receiver) {
		Query queryCount = new Query(Criteria.where(RECEIVER_FIELD).is(receiver));
		return mongoTemplate.count(queryCount, ALARM_COLLECTION);
	}


}
