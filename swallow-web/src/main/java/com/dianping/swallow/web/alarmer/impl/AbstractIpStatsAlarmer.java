package com.dianping.swallow.web.alarmer.impl;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.container.IpResourceContainer;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.stats.AbstractIpGroupStatsData;
import com.dianping.swallow.web.model.stats.AbstractIpStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qi.yin
 *         2016/01/12  上午10:05.
 */
public abstract class AbstractIpStatsAlarmer<K extends AbstractIpStatsData.IpStatsDataKey, I extends AbstractIpStatsData, G extends AbstractIpGroupStatsData<I>>
        extends AbstractStatsAlarmer {

    @Autowired
    protected EventReporter eventReporter;

    @Autowired
    protected EventFactory eventFactory;

    @Autowired
    protected ResourceContainer resourceContainer;

    @Autowired
    private IpResourceContainer ipResourceContainer;

    private Map<K, IpStatusData> ipStatusDatas = new ConcurrentHashMap<K, IpStatusData>();

    @Autowired
    protected IpAlarmerConfig alarmerConfig;

    protected long checkInterval = 10 * 60 * 1000;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        checkInterval = alarmerConfig.getCheckInterval() * 60 * 1000;
    }

    public void checkClusterStats(G ipGroupStatsData) {
        if (ipGroupStatsData == null) {
            return;
        }
        List<I> ipStatsDatas = ipGroupStatsData.getIpStatsDatas();
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return;
        }
        boolean hasGroupStatsData = ipGroupStatsData.hasStatsData(alarmerConfig.getAvgCountThreshold());
        for (I ipStatsData : ipStatsDatas) {
            boolean hasStatsData = ipStatsData.hasStatsData();
            @SuppressWarnings("unchecked")
            K key = (K) ipStatsData.createStatsDataKey();
            IpStatusData ipStatusData = ipStatusDatas.get(key);
            if (ipStatusData == null) {
                ipStatusData = new IpStatusData();
            }
            if (!hasStatsData) {
                if (hasGroupStatsData) {
                    ipStatusDatas.put(key, ipStatusData.updateNoData());
                } else {
                    if (ipStatsDatas.size() > 1) {
                        //不做处理
                    } else {
                        ipStatusDatas.put(key, ipStatusData.updateNoSubData());
                    }
                }
            } else {
                ipStatusDatas.put(key, ipStatusData.updateHasData());
            }
        }

    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public void alarmIpStatsData() {
        Iterator<Map.Entry<K, IpStatusData>> itStatusData = ipStatusDatas.entrySet().iterator();

        while (itStatusData.hasNext()) {
            Map.Entry<K, IpStatusData> statusDataEntry = itStatusData.next();
            K statsDataKey = statusDataEntry.getKey();
            IpStatusData ipStatusData = statusDataEntry.getValue();

            if (ipStatusData.getNoDataCount() > 0L) {

                if (getCurrentTimeMillis() - ipStatusData.getNoDataTime() > checkInterval) {
                    itStatusData.remove();
                    if (ipStatusData.getNoDataCount() > alarmerConfig.getNoHasDataCount()) {
                        report(statsDataKey);
                    }
                }
            } else if (ipStatusData.getNoSubDataCount() > 0L) {

                if (getCurrentTimeMillis() - ipStatusData.getNoSubDataTime() > checkInterval) {
                    itStatusData.remove();
                    checkNoClusterStats(statsDataKey);
                }
            }
        }
    }

    protected abstract void checkNoClusterStats(K statsDataKey);

    public void checkNoClusterStats0(K statsDataKey, List<I> ipStatsDatas) {
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return;
        }
        int hasDataCount = 0;

        for (I ipStatsData : ipStatsDatas) {
            if (ipStatsData.hasStatsData(alarmerConfig.getQpsThreshold())) {
                hasDataCount++;
            }
        }

        if (hasDataCount > alarmerConfig.getNoHasDataCount()) {
            report(statsDataKey);
        }
    }

    protected abstract boolean isReport(K statsDataKey);

    protected abstract void report(K statsDataKey);

    protected abstract G createIpGroupStatsData();

    protected Map<String, G> getIpGroupStatsData(List<I> ipStatsDatas) {
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return null;
        }

        Map<String, G> ipStatsDataMap = new HashMap<String, G>();

        for (I ipStatsData : ipStatsDatas) {

            String appName = ipResourceContainer.getApplicationName(ipStatsData.getIp());
            if (StringUtils.isBlank(appName)) {
                continue;
            }

            G ipGroupStatsData = null;

            if (ipStatsDataMap.containsKey(appName)) {
                ipGroupStatsData = ipStatsDataMap.get(appName);
            } else {
                ipGroupStatsData = createIpGroupStatsData();
                ipStatsDataMap.put(appName, ipGroupStatsData);
            }

            ipGroupStatsData.addIpStatsData(ipStatsData);
        }
        return ipStatsDataMap;
    }

    class IpStatusData {

        private long noDataTime;

        private long noDataCount;

        private long noSubDataTime;

        private long noSubDataCount;

        public IpStatusData updateNoData() {
            if (noDataCount != 0L) {
                noDataCount++;
            } else {
                noDataCount++;
                noDataTime = getCurrentTimeMillis();
            }
            return this;
        }

        public IpStatusData updateNoSubData() {
            if (noSubDataCount != 0L) {
                noSubDataCount++;
            } else {
                noSubDataCount++;
                noSubDataTime = getCurrentTimeMillis();
            }
            return this;
        }

        public IpStatusData updateHasData() {
            this.noDataCount = 0L;
            this.noSubDataCount = 0L;
            return this;
        }

        public long getNoDataTime() {
            return noDataTime;
        }

        public long getNoDataCount() {
            return noDataCount;
        }

        public long getNoSubDataTime() {
            return noSubDataTime;
        }

        public long getNoSubDataCount() {
            return noSubDataCount;
        }
    }

}
