package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.dao.ServerReportDao;
import org.springframework.stereotype.Component;

/**
 * Author   mingdongli
 * 16/1/22  上午11:34.
 */
@Component
public class DefaultConsumerServerReportDao extends AbstractServerReportDao implements ServerReportDao {

    @Override
    public String getCollection() {
        return "CONSUMER_SERVER_REPORT";
    }
}
