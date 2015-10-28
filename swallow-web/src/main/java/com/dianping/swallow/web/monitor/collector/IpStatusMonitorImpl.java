package com.dianping.swallow.web.monitor.collector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.web.model.stats.AbstractIpStatsData;
import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.resource.IpInfo;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月8日 上午11:06:00
 */
public class IpStatusMonitorImpl<T, K extends AbstractIpStatsData> implements IpStatusMonitor<T, K> {

    private static final long ACTIVE_TIMESPAN = 24 * 60 * 60 * 1000;

    private Map<T, ActiveIpData> activeIpDatas = new ConcurrentHashMap<T, ActiveIpData>();

    private Map<T, List<IpInfo>> lastIpInfoDatas = new ConcurrentHashMap<T, List<IpInfo>>();

    private final long startTimestamp = System.currentTimeMillis();

    public static class ActiveIpData {

        private Map<String, Long> activeDatas = new ConcurrentHashMap<String, Long>();

        public void addData(String ip, boolean hasData) {
            if (hasData) {
                activeDatas.put(ip, System.currentTimeMillis());
            }
        }

        public boolean isIpInActive(String ip) {
            if (activeDatas.containsKey(ip)) {
                return System.currentTimeMillis() - activeDatas.get(ip) > ACTIVE_TIMESPAN;
            }
            return true;
        }
    }

    public boolean isValid() {
        return System.currentTimeMillis() - startTimestamp > ACTIVE_TIMESPAN;
    }

    public void putActiveIpDatas(T key, List<K> ipStatsDatas) {
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return;
        }
        ActiveIpData activeIpData = null;
        if (activeIpDatas.containsKey(key)) {
            activeIpData = activeIpDatas.get(key);
        } else {
            activeIpData = new ActiveIpData();
            activeIpDatas.put(key, activeIpData);
        }
        for (K ipStatsData : ipStatsDatas) {
            activeIpData.addData(ipStatsData.getIp(), ipStatsData.hasStatsData());
        }

    }

    @Override
    public boolean isChanged(T key, List<IpInfo> currIpInfos) {
        if (lastIpInfoDatas.containsKey(key)) {
            List<IpInfo> lastIpInfos = lastIpInfoDatas.get(key);
            if (currIpInfos == null && lastIpInfos != null) {
                return true;
            } else if (currIpInfos != null && lastIpInfos == null) {
                return true;
            } else if (currIpInfos == null && lastIpInfos == null) {
                return false;
            } else {
                if (currIpInfos.size() != lastIpInfos.size()) {
                    return true;
                } else {
                    for (IpInfo currIpInfo : currIpInfos) {
                        boolean isEqual = false;
                        for (IpInfo lastIpInfo : lastIpInfos) {
                            if (currIpInfo.equals(lastIpInfo)) {
                                isEqual = true;
                                break;
                            }
                        }
                        if (!isEqual) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isNeedLoaded(T key) {
        return !lastIpInfoDatas.containsKey(key);
    }

    @Override
    public void setLastIpInfos(T key, List<IpInfo> lastIpInfos) {
        lastIpInfoDatas.put(key, lastIpInfos);
    }

    @Override
    public List<IpInfo> getLastIpInfos(T key) {
        return lastIpInfoDatas.get(key);
    }

    public Set<String> getInActiveIps(T key) {
        Set<String> inActiveIps = new HashSet<String>();
        if (activeIpDatas.containsKey(key)) {
            ActiveIpData activeIpData = activeIpDatas.get(key);
            if (activeIpData != null) {
                for (String ip : activeIpData.activeDatas.keySet()) {
                    if (StringUtils.isNotBlank(ip)) {
                        if (activeIpData.isIpInActive(ip)) {
                            inActiveIps.add(ip);
                        }
                    }
                }

            }
        }
        return inActiveIps;
    }

    public Set<String> getAllIps(T key) {
        Set<String> allIps = new HashSet<String>();
        if (activeIpDatas.containsKey(key)) {
            ActiveIpData activeIpData = activeIpDatas.get(key);
            if (activeIpData != null) {
                for (String ip : activeIpData.activeDatas.keySet()) {
                    if (StringUtils.isNotBlank(ip)) {
                        allIps.add(ip);
                    }
                }

            }
        }
        return allIps;
    }

    public List<IpInfo> getRelatedIpInfo(T key) {
        List<IpInfo> ipInfos = getLastIpInfos(key);
        if (ipInfos == null) {
            ipInfos = new ArrayList<IpInfo>();
        }
        Set<String> allIps = this.getAllIps(key);
        if (allIps != null && !allIps.isEmpty()) {
            for (String statsDataIp : allIps) {
                boolean isHasIp = false;
                for (IpInfo ipInfo : ipInfos) {
                    if (statsDataIp.equals(ipInfo.getIp())) {
                        isHasIp = true;
                        break;
                    }
                }
                if (!isHasIp) {
                    ipInfos.add(new IpInfo(statsDataIp, true, true));
                }
            }
        }
        if (this.isValid()) {
            for (IpInfo ipInfo : ipInfos) {
                ipInfo.setActive(true);
            }
            Set<String> inActiveIps = this.getInActiveIps(key);
            if (inActiveIps != null && !inActiveIps.isEmpty()) {
                for (String inActiveIp : inActiveIps) {
                    for (IpInfo ipInfo : ipInfos) {
                        if (inActiveIp.equals(ipInfo.getIp())) {
                            ipInfo.setActive(false);
                            break;
                        }
                    }
                }
            }
        }
        return ipInfos;
    }

}
