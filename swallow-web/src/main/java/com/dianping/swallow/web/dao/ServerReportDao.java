package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.model.report.ServerReport;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/21  下午8:25.
 */
public interface ServerReportDao {

    boolean insert(ServerReport serverReport);

    List<ServerReport> find(String ip, long startKey, long endKey);

    List<ServerReport> find(long startKey, long endKey);

    ServerReport find(Long timeKey);

    ServerReport firstServerReport();

    String getCollection();

}
