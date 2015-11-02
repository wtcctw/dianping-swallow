package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.AlarmMeta;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:48:22
 */
public interface AlarmMetaService {

	boolean insert(AlarmMeta alarmMeta);

	boolean update(AlarmMeta alarmMeta);

	int deleteByMetaId(int metaId);

	AlarmMeta findByMetaId(int metaId);

	List<AlarmMeta> findByPage(int offset, int limit);
}
