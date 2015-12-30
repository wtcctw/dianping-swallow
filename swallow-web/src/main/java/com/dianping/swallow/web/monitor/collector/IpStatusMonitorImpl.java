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

    private final long startTimestamp = System.currentTimeMillis();

    public static class ActiveIpData {

        private Map<String, Long> activeDatas = new ConcurrentHashMap<String, Long>();

        public void addData(String ip, boolean hasData) {
            if (hasData) {
                activeDatas.put(ip, System.currentTimeMillis());
            }
        }

        public boolean isIpActive(String ip) {
            if (activeDatas.containsKey(ip)) {
                return System.currentTimeMillis() - activeDatas.get(ip) < ACTIVE_TIMESPAN;
            }
            return false;
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
            for (K ipStatsData : ipStatsDatas) {
                activeIpData.addData(ipStatsData.getIp(), ipStatsData.hasStatsData());
            }
        } else {
            activeIpData = new ActiveIpData();
            for (K ipStatsData : ipStatsDatas) {
                activeIpData.addData(ipStatsData.getIp(), true);
            }
            activeIpDatas.put(key, activeIpData);
        }
    }

    @Override
    public boolean isChanged(List<IpInfo> lastIpInfos, List<IpInfo> currIpInfos) {
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

    public Set<String> getActiveIps(T key) {
        Set<String> activeIps = new HashSet<String>();
        if (activeIpDatas.containsKey(key)) {
            ActiveIpData activeIpData = activeIpDatas.get(key);
            if (activeIpData != null) {
                for (String ip : activeIpData.activeDatas.keySet()) {
                    if (StringUtils.isNotBlank(ip)) {
                        if (activeIpData.isIpActive(ip)) {
                            activeIps.add(ip);
                        }
                    }
                }

            }
        }
        return activeIps;
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

    public List<IpInfo> getRelatedIpInfo(T key, List<IpInfo> lastIpInfos) {
        List<IpInfo> ipInfos = new ArrayList<IpInfo>();
        if (!this.isValid()) {
            if (lastIpInfos != null) {
                ipInfos.addAll(lastIpInfos);
            }

        } else {
            if (lastIpInfos != null) {
                for (IpInfo ipInfo : lastIpInfos) {
                    if (!ipInfo.isAlarm()) {
                        ipInfos.add(ipInfo);
                    }
                }
            }
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
                ipInfo.setActive(false);
            }
            Set<String> activeIps = this.getActiveIps(key);
            if (activeIps != null && !activeIps.isEmpty()) {
                for (String activeIp : activeIps) {
                    for (IpInfo ipInfo : ipInfos) {
                        if (activeIp.equals(ipInfo.getIp())) {
                            ipInfo.setActive(true);
                            break;
                        }
                    }
                }
            }
        }

        return ipInfos;
    }

}
