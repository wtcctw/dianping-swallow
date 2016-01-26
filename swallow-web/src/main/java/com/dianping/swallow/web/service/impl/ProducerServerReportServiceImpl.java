package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.dao.ProducerServerResourceDao;
import com.dianping.swallow.web.dao.ProducerServerStatsDataDao;
import com.dianping.swallow.web.dao.ServerReportDao;
import com.dianping.swallow.web.dao.impl.DefaultProducerServerReportDao;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.ServerReportService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   mingdongli
 * 16/1/22  上午9:45.
 */
@Service("producerServerReportService")
public class ProducerServerReportServiceImpl extends AbstractServerReportService implements ServerReportService {

    @Autowired
    private DefaultProducerServerReportDao producerServerReportDao;

    @Autowired
    private ProducerServerStatsDataDao producerServerStatsDataDao;

    @Autowired
    private ProducerServerResourceDao producerServerResourceDao;

    @Override
    protected boolean needBuild() {
        firstServerReport = producerServerReportDao.firstServerReport();

        if (firstServerReport == null) {
            return true;
        }
        ProducerServerStatsData producerServerStatsData = producerServerStatsDataDao.findOldestData();
        if (producerServerStatsData == null) {
            return false;
        }
        long oldestTimeForPSSD = producerServerStatsData.getTimeKey() * 5 * MILLISECOND_TO_SCEOND;
        long oldestTimeForPSR = firstServerReport.getTimeKey();
        if (oldestTimeForPSR - oldestTimeForPSSD > milliSecondOfOneDay()) {
            return true;
        }
        return false;
    }

    @Override
    protected void doBuild(long startTimeKey, long endTimeKey) {

        Long totalServerCount = 0L;
        List<String> ipList = getProducerServerIp();
        for (String ip : ipList) {
            if(DEFAULT.equalsIgnoreCase(ip)){
                continue;
            }

            List<ProducerServerStatsData> pssdList = producerServerStatsDataDao.findSectionData(ip, narrowTime(startTimeKey) , narrowTime(endTimeKey));
            Long qps = 0L;
            for (ProducerServerStatsData sr : pssdList) {
                qps += sr.getQps();
            }
            Long totalCount = qps * SAMPLE_INTERVAL;
            totalServerCount += totalCount;
            storeAndCache(ip, totalCount, endTimeKey);
        }
        storeAndCache(TOTAL, totalServerCount, endTimeKey);

    }

    @Override
    protected ServerReportDao getDao() {
        return producerServerReportDao;
    }

    @Override
    protected String getClazz(){
        return this.getClass().getSimpleName();
    }

    private List<String> getProducerServerIp() {

        List<String> ipList = new ArrayList<String>();
        List<ProducerServerResource> producerServerResources = producerServerResourceDao.findAll();
        if (producerServerResources != null) {
            for (ProducerServerResource psr : producerServerResources) {
                String ip = psr.getIp();
                if (StringUtils.isNotBlank(ip) && !ipList.contains(ip)) {
                    ipList.add(ip);
                }
            }
        }
        return ipList;
    }

}
