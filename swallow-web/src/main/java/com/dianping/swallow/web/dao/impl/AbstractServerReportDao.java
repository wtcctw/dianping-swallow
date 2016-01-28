package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.dao.ServerReportDao;
import com.dianping.swallow.web.model.report.ServerReport;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/22  上午9:48.
 */
public abstract class AbstractServerReportDao extends AbstractStatsDao implements ServerReportDao{

    private static final String IP = "ip";

    private static final String TIMEKEY = "timeKey";

    @Override
    public boolean insert(ServerReport serverReport) {
        try {
            mongoTemplate.save(serverReport, getCollection());
            return true;
        } catch (Exception e) {
            logger.error("[insert] error when save server report." + serverReport, e);
        }
        return false;
    }

    @Override
    public List<ServerReport> find(String ip, long startKey, long endKey) {
        Query query = new Query(Criteria.where(IP).is(ip).and(TIMEKEY).gte(startKey)
                .lte(endKey)).with(new Sort(new Sort.Order(Sort.Direction.ASC, TIMEKEY)));
        List<ServerReport> serverReports = mongoTemplate.find(query, ServerReport.class, getCollection());
        return serverReports;
    }

    @Override
    public List<ServerReport> find(long startKey, long endKey) {
        Query query = new Query(Criteria.where(TIMEKEY).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(
                Sort.Direction.ASC, TIMEKEY)));
        List<ServerReport> serverReports = mongoTemplate.find(query, ServerReport.class, getCollection());
        return serverReports;
    }

    @Override
    public ServerReport firstServerReport(){
        Query query = new Query();
        query.skip(0).limit(1).with(new Sort(new Sort.Order(Sort.Direction.ASC, TIMEKEY)));
        ServerReport serverReport = mongoTemplate.findOne(query, ServerReport.class, getCollection());
        return serverReport;
    }

    @Override
    public ServerReport find(Long timeKey){
        Query query = new Query(Criteria.where(TIMEKEY).is(timeKey));
        ServerReport serverReport = mongoTemplate.findOne(query, ServerReport.class, getCollection());
        return serverReport;
    }
}
