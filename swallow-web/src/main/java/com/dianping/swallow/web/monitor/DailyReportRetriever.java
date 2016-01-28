package com.dianping.swallow.web.monitor;

import com.dianping.swallow.web.model.event.StatisType;

import java.util.Map;

/**
 * Author   mingdongli
 * 16/1/22  下午2:10.
 */
public interface DailyReportRetriever {

    int DAILY_INTERVAL_SECOND = 24 * 60 * 60;

    Map<String, StatsData> getProducerServerMessageCount(long start, long end);

    Map<String, StatsData> getProducerServerMessageCount();

    Map<String, StatsData> getConsumerServerMessageCount(long start, long end);

    Map<String, StatsData> getConsumerServerMessageCount();

    StatsDataDesc createServerDesc(String ip, StatisType type);
}
