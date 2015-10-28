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

    private Map<T, Long> firstCandidates = new ConcurrentHashMap<T, Long>();

    private Map<T, Long> secondCandidates = new ConcurrentHashMap<T, Long>();

    private Map<T, Long> whiteLists = new ConcurrentHashMap<T, Long>();

    protected long checkInterval = 10 * 60 * 1000;

    protected long qpsThreshold = 20;

    public void checkIpGroup(X ipGroupStatsData) {
        if (ipGroupStatsData == null) {
            return;
        }
        List<K> ipStatsDatas = ipGroupStatsData.getIpStatsDatas();
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return;
        }
        boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
        for (K ipStatsData : ipStatsDatas) {
            boolean hasStatsData = ipStatsData.hasStatsData();
            @SuppressWarnings("unchecked")
            T key = (T) ipStatsData.createStatsDataKey();
            if (!hasStatsData) {
                if (hasGroupStatsData) {
                    if (!firstCandidates.containsKey(key)) {
                        firstCandidates.put(key, System.currentTimeMillis());
                    } else {
                        if (whiteLists.containsKey(key) && whiteLists.get(key) > firstCandidates.get(key)) {
                            firstCandidates.put(key, System.currentTimeMillis());
                        }
                    }
                } else {
                    if (ipStatsDatas.size() == 1) {
                        if (!secondCandidates.containsKey(key)) {
                            secondCandidates.put(key, System.currentTimeMillis());
                        } else {
                            if (whiteLists.containsKey(key) && whiteLists.get(key) > secondCandidates.get(key)) {
                                secondCandidates.put(key, System.currentTimeMillis());
                            }
                        }
                    }
                }
            } else {
                whiteLists.put(key, System.currentTimeMillis());
            }
        }
    }

    public void alarmSureRecords() {
        Iterator<Entry<T, Long>> iterator = firstCandidates.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<T, Long> checkRecord = iterator.next();
            T statsDataKey = checkRecord.getKey();
            long lastRecordTime = checkRecord.getValue();
            if (System.currentTimeMillis() - lastRecordTime < checkInterval) {
                continue;
            }
            iterator.remove();
            report(statsDataKey);
        }
    }

    public void alarmUnSureRecords() {
        Iterator<Entry<T, Long>> statsDataIterator = secondCandidates.entrySet().iterator();
        while (statsDataIterator.hasNext()) {
            Entry<T, Long> checkRecord = statsDataIterator.next();
            T statsDataKey = checkRecord.getKey();
            long lastRecordTime = checkRecord.getValue();

            if (System.currentTimeMillis() - lastRecordTime < checkInterval) {
                continue;
            }
            statsDataIterator.remove();
            checkUnSureLastRecords(statsDataKey);
        }
    }

    protected abstract void checkUnSureLastRecords(T statsDataKey);

    protected abstract boolean isReport(T statsDataKey);

    protected abstract void report(T statsDataKey);

}