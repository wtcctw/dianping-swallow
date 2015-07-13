package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerServerStatsData;

/**
 *
 * @author qiyin
 *
 */
public interface ProducerServerStatisDataDao {

	public boolean insert(ProducerServerStatsData statisData);

	public boolean update(ProducerServerStatsData statisData);

	public int deleteById(String id);

	public ProducerServerStatsData findById(String id);

	public ProducerServerStatsData findByTimeKey(long timeKey);

	public List<ProducerServerStatsData> findSectionData(long startKey, long endKey);

}
