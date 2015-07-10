package com.dianping.swallow.web.monitor.wapper;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;

public interface ProducerDataWapper {
	
	ProducerServerStatsData getServerStatsData(long timeKey);
	
	List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey);

}
