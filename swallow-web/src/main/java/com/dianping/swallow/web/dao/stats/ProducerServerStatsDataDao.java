package com.dianping.swallow.web.dao.stats;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerServerStatsData;

public interface ProducerServerStatsDataDao {

	public boolean insert(ProducerServerStatsData statisData);

	public boolean update(ProducerServerStatsData statisData);

	public int deleteById(String id);

	public ProducerServerStatsData findById(String id);

	public ProducerServerStatsData findByTimeKey(String ip, long timeKey);

	public List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey);
}
