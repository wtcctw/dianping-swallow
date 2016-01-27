package com.dianping.swallow.web.container;

import com.dianping.swallow.web.model.report.ServerReport;
import com.dianping.swallow.web.service.impl.AbstractServerReportService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 16/1/22  下午3:30.
 */
@Component
@Scope("prototype")
public class ServerReportStatsDataContainerImpl implements ServerReportStatsDataContainer {

    private NavigableMap<Long, ServerReport> serverReportMap = new ConcurrentSkipListMap<Long, ServerReport>();

    @Override
    public void add(ServerReport serverReport) {

        long time = serverReport.getTimeKey();
        ServerReport value = serverReportMap.get(time);

        if (value == null) {
            if (serverReportMap.isEmpty()) {
                serverReportMap.put(time, serverReport);
                return;
            }
            long eldestTimeKey = serverReportMap.firstKey();
            long endKey = AbstractServerReportService.getEndKeyForPastSixMonth();
            if (time >= eldestTimeKey && eldestTimeKey <= endKey) {
                serverReportMap.remove(eldestTimeKey); //不能把历史的数据加入
                serverReportMap.put(time, serverReport);
            }
        }
    }

    @Override
    public NavigableMap<Long, Long> retrieve() {

        if (serverReportMap == null) {
            return null;
        }

        NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
        for (Map.Entry<Long, ServerReport> entry : serverReportMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getCount());
        }

        return result;
    }
}
