package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.report.ServerReport;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 16/1/21  下午10:37.
 */
public interface ServerReportService {

    boolean insert(ServerReport serverReport);

    List<ServerReport> find(String ip);

    List<ServerReport> find(String ip, long startKey, long endKey);

    Map<String, NavigableMap<Long, Long>> find(long startKey, long endKey);

    long firstServerReportTimeInMemory();

    Map<String, NavigableMap<Long, Long>> retrieve();
}
