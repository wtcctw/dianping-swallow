package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.AlarmMeta;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:32:26
 */
public interface AlarmMetaDao {

	boolean insert(AlarmMeta alarmMeta);

	boolean update(AlarmMeta alarmMeta);

	int deleteById(String id);

	int deleteByMetaId(int metaId);

	AlarmMeta findById(String id);

	AlarmMeta findByMetaId(int metaId);

	List<AlarmMeta> findByPage(int offset, int limit);

}
