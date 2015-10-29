package com.dianping.swallow.web.alarmer.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.stats.AbstractIpGroupStatsData;
import com.dianping.swallow.web.model.stats.AbstractIpStatsData;
import com.dianping.swallow.web.model.stats.AbstractIpStatsData.IpStatsDataKey;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月19日 下午7:32:10
 */
public abstract class AbstractIpStatsAlarmer<T extends IpStatsDataKey, K extends AbstractIpStatsData, X extends AbstractIpGroupStatsData<K>>
        extends AbstractStatsAlarmer {

    @Autowired
    protected EventReporter eventReporter;

    @Autowired
    protected EventFactory eventFactory;

    @Autowired
    protected ResourceContainer resourceContainer;

    private Map<T, IpStatusData> ipStatusDatas = new ConcurrentHashMap<T, IpStatusData>();

    protected long checkInterval = 10 * 60 * 1000;

    protected long qpsThreshold = 3;

    protected long totalThreshold = 15;

    public void checkIpGroupStats(X ipGroupStatsData) {
        if (ipGroupStatsData == null) {
            return;
        }
        List<K> ipStatsDatas = ipGroupStatsData.getIpStatsDatas();
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return;
        }
        boolean hasGroupStatsData = ipGroupStatsData.hasStatsData(qpsThreshold, totalThreshold);
        for (K ipStatsData : ipStatsDatas) {
            boolean hasStatsData = ipStatsData.hasStatsData();
            T key = (T) ipStatsData.createStatsDataKey();
            IpStatusData ipStatusData = ipStatusDatas.get(key);
            if (ipStatusData == null) {
                ipStatusData = new IpStatusData();
            }
            if (!hasStatsData) {
                if (hasGroupStatsData) {
                    ipStatusDatas.put(key, ipStatusData.updateNoDataTime(getCurrentTimeMillis()));
                } else {
                    if (ipStatsDatas.size() > 1) {
                        //都没有数据不考虑
                    } else {
                        ipStatusDatas.put(key, ipStatusData.updateSubNoDataTime(getCurrentTimeMillis()));
                    }
                }
            } else {
                ipStatusDatas.put(key, ipStatusData.setHasDataTime(getCurrentTimeMillis()));
            }
        }

    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public void alarmIpStatsData() {
        Iterator<Entry<T, IpStatusData>> itStatusData = ipStatusDatas.entrySet().iterator();
        while (itStatusData.hasNext()) {
            Entry<T, IpStatusData> statusDataEntry = itStatusData.next();
            T statsDataKey = statusDataEntry.getKey();
            IpStatusData ipStatusData = statusDataEntry.getValue();
            if (ipStatusData.getNoDataCount() > 0) {
                if (getCurrentTimeMillis() - ipStatusData.getNoDataTime() > checkInterval) {
                    itStatusData.remove();
                    report(statsDataKey);
                }
            } else if (ipStatusData.getSubNoDataCount() > 0) {
                if (getCurrentTimeMillis() - ipStatusData.getNoDataTime() > checkInterval) {
                    itStatusData.remove();
                    checkUnSureLastRecords(statsDataKey);
                }
            }
        }
    }

    protected abstract void checkUnSureLastRecords(T statsDataKey);

    protected abstract boolean isReport(T statsDataKey);

    protected abstract void report(T statsDataKey);

    class IpStatusData {

        private long hasDataTime;

        private long noDataTime;

        private int noDataCount;

        private long subNoDataTime;

        private int subNoDataCount;

        public IpStatusData updateNoDataTime(long currentTimeMillis) {
            if (noDataCount != 0) {
                if (noDataTime < hasDataTime) {
                    noDataCount = 0;
                    noDataTime = currentTimeMillis;
                } else {
                    noDataCount++;
                }
            } else {
                noDataCount++;
                noDataTime = currentTimeMillis;
            }
            return this;
        }

        public IpStatusData updateSubNoDataTime(long currentTimeMillis) {
            if (subNoDataCount == 0) {
                if (subNoDataTime < hasDataTime) {
                    subNoDataCount = 0;
                    subNoDataTime = currentTimeMillis;
                } else {
                    subNoDataCount++;
                }
            } else {
                subNoDataCount++;
                subNoDataTime = currentTimeMillis;
            }
            return this;
        }

        public long getHasDataTime() {
            return hasDataTime;
        }

        public IpStatusData setHasDataTime(long hasDataTime) {
            this.hasDataTime = hasDataTime;
            return this;
        }

        public long getNoDataTime() {
            return noDataTime;
        }

        public IpStatusData setNoDataTime(long noDataTime) {
            this.noDataTime = noDataTime;
            return this;
        }

        public int getNoDataCount() {
            return noDataCount;
        }

        public IpStatusData setNoDataCount(int noDataCount) {
            this.noDataCount = noDataCount;
            return this;
        }

        public long getSubNoDataTime() {
            return subNoDataTime;
        }

        public IpStatusData setSubNoDataTime(long subNoDataTime) {
            this.subNoDataTime = subNoDataTime;
            return this;
        }

        public int getSubNoDataCount() {
            return subNoDataCount;
        }

        public IpStatusData setSubNoDataCount(int subNoDataCount) {
            this.subNoDataCount = subNoDataCount;
            return this;
        }
    }

}