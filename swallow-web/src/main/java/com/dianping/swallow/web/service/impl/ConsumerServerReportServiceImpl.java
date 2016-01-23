package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.dao.ConsumerServerResourceDao;
import com.dianping.swallow.web.dao.ConsumerServerStatsDataDao;
import com.dianping.swallow.web.dao.ServerReportDao;
import com.dianping.swallow.web.dao.impl.DefaultConsumerServerReportDao;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.service.ServerReportService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author   mingdongli
 * 16/1/22  上午11:35.
 */
@Service("consumerServerReportService")
public class ConsumerServerReportServiceImpl extends AbstractServerReportService implements ServerReportService {

    @Autowired
    private DefaultConsumerServerReportDao consumerServerReportDao;

    @Autowired
    private ConsumerServerStatsDataDao consumerServerStatsDataDao;

    @Autowired
    private ConsumerServerResourceDao consumerServerResourceDao;

    @Override
    protected boolean needBuild() {
        firstServerReport = consumerServerReportDao.firstServerReport();

        if (firstServerReport == null) {
            return true;
        }
        ConsumerServerStatsData consumerServerStatsData = consumerServerStatsDataDao.findOldestData();
        if (consumerServerStatsData == null) {
            return false;
        }
        long oldestTimeForCSSD = consumerServerStatsData.getTimeKey() * 5 * MILLISECOND_TO_SCEOND;
        long oldestTimeForPSR = firstServerReport.getTimeKey();
        if (oldestTimeForPSR - oldestTimeForCSSD > milliSecondOfOneDay()) {
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
            List<ConsumerServerStatsData> cssdList = consumerServerStatsDataDao.findSectionData(ip, startTimeKey, endTimeKey);
            Long totalQps = 0L;
            for (ConsumerServerStatsData sr : cssdList) {
                totalQps += sr.getSendQps();
            }
            Long totalCount = totalQps * SAMPLE_INTERVAL;
            totalServerCount += totalCount;
            storeAndCache(ip, totalCount, endTimeKey);
        }
        storeAndCache(TOTAL, totalServerCount, endTimeKey);

    }

    @Override
    protected ServerReportDao getDao() {
        return consumerServerReportDao;
    }

    @Override
    protected String getClazz(){
        return this.getClass().getSimpleName();
    }

    private List<String> getProducerServerIp() {

        List<String> ipList = new ArrayList<String>();
        List<ConsumerServerResource> consumerServerResources = consumerServerResourceDao.findAll();
        if (consumerServerResources != null) {
            for (ConsumerServerResource csr : consumerServerResources) {
                String ip = csr.getIp();
                if (StringUtils.isNotBlank(ip) && !ipList.contains(ip)) {
                    ipList.add(ip);
                }
            }
        }
        return ipList;
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        System.out.println(calendar.getTime());
        long endKey = calendar.getTimeInMillis() - 1000L;
        System.out.println(new Date(endKey));
        System.out.println(String.format("%tD", new Date(endKey)));
    }

}
