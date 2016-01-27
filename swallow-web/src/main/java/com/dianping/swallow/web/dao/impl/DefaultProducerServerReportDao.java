package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.dao.ServerReportDao;
import org.springframework.stereotype.Component;

/**
 * Author   mingdongli
 * 16/1/22  上午9:51.
 */
@Component
public class DefaultProducerServerReportDao extends AbstractServerReportDao implements ServerReportDao{

    @Override
    public String getCollection() {
        return "PRODUCER_SERVER_REPORT";
    }
}
