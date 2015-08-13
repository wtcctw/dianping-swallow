package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AlarmMetaDao;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.service.AlarmMetaService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月12日 下午6:21:45
 */
@Service("alarmMetaService")
public class AlarmMetaServiceImpl implements AlarmMetaService {

	@Autowired
	private AlarmMetaDao alarmMetaDao;

	@Override
	public boolean insert(AlarmMeta alarmMeta) {
		return alarmMetaDao.insert(alarmMeta);
	}

	@Override
	public boolean update(AlarmMeta alarmMeta) {
		AlarmMeta tempAlarmMeta = null;
		tempAlarmMeta = findByMetaId(alarmMeta.getMetaId());
		if (tempAlarmMeta == null) {
			return insert(alarmMeta);
		} else {
			alarmMeta.setId(tempAlarmMeta.getId());
			return alarmMetaDao.update(alarmMeta);
		}
	}

	@Override
	public int deleteById(String id) {
		return alarmMetaDao.deleteById(id);
	}

	@Override
	public int deleteByMetaId(int metaId) {
		return alarmMetaDao.deleteByMetaId(metaId);
	}

	@Override
	public AlarmMeta findById(String id) {
		return alarmMetaDao.findById(id);
	}

	@Override
	public AlarmMeta findByMetaId(int metaId) {
		return alarmMetaDao.findByMetaId(metaId);
	}

	@Override
	public List<AlarmMeta> findByPage(int offset, int limit) {
		return alarmMetaDao.findByPage(offset, limit);
	}

}
