package com.dianping.swallow.web.container;

import com.dianping.swallow.web.model.report.ServerReport;

import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 16/1/22  下午3:28.
 */
public interface ServerReportStatsDataContainer {

    void add(ServerReport serverReport);

    NavigableMap<Long, Long> retrieve();

}
