package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.event.StatisType;
import com.dianping.swallow.web.monitor.DailyReportRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ServerReportService;
import com.dianping.swallow.web.service.impl.AbstractServerReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 16/1/22  下午2:14.
 */
@Component
public class DefaultDailyReportRetriever extends AbstractMonitorDataRetriever<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> implements DailyReportRetriever {

    @Resource(name = "producerServerReportService")
    private ServerReportService producerServerReportService;

    @Resource(name = "consumerServerReportService")
    private ServerReportService consumerServerReportService;

    @Value("${swallow.web.monitor.report.monthsize}")
    public static int monthSize = 6;

    @Override
    protected long getDefaultStart() {
        return AbstractServerReportService.getEndKeyForPastXMonth(monthSize);
    }

    @Override
    protected long getDefaultEnd() {
        return AbstractServerReportService.getYesterdayEndKey();
    }

    @Override
    public Map<String, StatsData> getProducerServerMessageCount(long start, long end) {

        Pair<Long, Long> pair = normalizeTime(start, end);
        start = pair.getFirst();
        end = pair.getSecond();
        if (dataExistInMemory(producerServerReportService, start)) {
            return getServerReportInMemory(producerServerReportService, start, end);
        }

        return getServerReportInDb(producerServerReportService, start, end);
    }

    private Map<String, StatsData> getServerReportInMemory(ServerReportService serverReportService, long start, long end) {

        Map<String, StatsData> result = new HashMap<String, StatsData>();

        Map<String, NavigableMap<Long, Long>> serverReports = serverReportService.retrieve();

        for (Map.Entry<String, NavigableMap<Long, Long>> entry : serverReports.entrySet()) {

            String serverIp = entry.getKey();
            NavigableMap<Long, Long> serverQps = entry.getValue();
            if (serverQps != null) {
                serverQps = serverQps.subMap(start, true, end, true);
            }
            result.put(serverIp, createStatsData(createServerDesc(serverIp, null), serverQps, start, end));
        }

        return result;
    }

    private Map<String, StatsData> getServerReportInDb(ServerReportService serverReportService, long start, long end) {
        Map<String, StatsData> result = new HashMap<String, StatsData>();

        Map<String, NavigableMap<Long, Long>> statsDataMaps = serverReportService.find(start, end);

        for (Map.Entry<String, NavigableMap<Long, Long>> statsDataMap : statsDataMaps.entrySet()) {
            String serverIp = statsDataMap.getKey();

            NavigableMap<Long, Long> statsData = statsDataMap.getValue();
            statsData = fillStatsData(statsData, start, end);
            result.put(serverIp, createStatsData(createServerDesc(serverIp, null), statsData, start, end));

        }
        return result;
    }

    private long sampleSize(long start, long end) {
        return (end - start) / AbstractServerReportService.milliSecondOfOneDay();
    }

    @Override
    protected NavigableMap<Long, Long> fillStatsData(NavigableMap<Long, Long> statsDatas, long start, long end) {
        int size = statsDatas.size();
        long sampleSize = sampleSize(start, end);
        if (size < sampleSize) {
            while (start <= end) {
                if (statsDatas.get(start) == null) {
                    statsDatas.put(start, 0L);
                }
                start += AbstractServerReportService.milliSecondOfOneDay();
            }
        }
        return statsDatas;
    }

    @Override
    protected int getSampleIntervalTime() {

        return DAILY_INTERVAL_SECOND;
    }

    @Override
    protected long getStartTime(NavigableMap<Long, Long> rawData, long start, long end) {
        return rawData == null ? end : start;
    }

    @Override
    public Map<String, StatsData> getProducerServerMessageCount() {
        return getProducerServerMessageCount(getDefaultStart(), getDefaultEnd());
    }

    @Override
    public Map<String, StatsData> getConsumerServerMessageCount(long start, long end) {

        Pair<Long, Long> pair = normalizeTime(start, end);
        start = pair.getFirst();
        end = pair.getSecond();
        if (dataExistInMemory(consumerServerReportService, start)) {
            return getServerReportInMemory(consumerServerReportService, start, end);
        }

        return getServerReportInDb(consumerServerReportService, start, end);
    }

    @Override
    public Map<String, StatsData> getConsumerServerMessageCount() {
        return getConsumerServerMessageCount(getDefaultStart(), getDefaultEnd());
    }

    @Override
    public StatsDataDesc createServerDesc(String ip, StatisType type) {
        return new ProducerServerDataDesc(ip, null, StatisDetailType.MSG_SEND);
    }

    @Override
    protected AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> createServerStatis() {
        return new ProducerAllData();
    }

    @Override
    protected StatsDataDesc createServerQpxDesc(String serverIp, com.dianping.swallow.common.server.monitor.data.StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createServerDelayDesc(String serverIp, com.dianping.swallow.common.server.monitor.data.StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createDelayDesc(String topic, com.dianping.swallow.common.server.monitor.data.StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createQpxDesc(String topic, com.dianping.swallow.common.server.monitor.data.StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    private boolean dataExistInMemory(ServerReportService serverReportService, long start) {

        long startKey = serverReportService.firstServerReportTimeInMemory();
        if (startKey <= start) {
            return true;
        }

        return false;

    }

    private Pair<Long, Long> normalizeTime(long start, long end) {
        start = AbstractServerReportService.normalizeStartTime(start);
        end = AbstractServerReportService.normalizeStartTime(end);
        return new Pair<Long, Long>(start, end);
    }

}
