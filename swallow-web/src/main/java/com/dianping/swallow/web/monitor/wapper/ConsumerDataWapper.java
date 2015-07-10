package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;

/**
 *
 * @author qiyin
 *
 */
public interface ConsumerDataWapper {

	ConsumerServerStatsData getServerStatsData(long timeKey);

	List<ConsumerTopicStatsData> getTopicStatsData(long timeKey);

	Map<String, List<ConsumerIdStatsData>> getConsumerIdStatsData(long timeKey);

	List<ConsumerIdStatsData> getConsumerIdStatsData(String topicName, long timeKey);

}
