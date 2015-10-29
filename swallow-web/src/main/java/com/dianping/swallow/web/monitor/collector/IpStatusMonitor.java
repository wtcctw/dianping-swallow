package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.stats.AbstractIpStatsData;

import java.util.List;

/**
 * @author qi.yin
 *         2015/10/27  上午11:07.
 */
public interface IpStatusMonitor<T, K extends AbstractIpStatsData> {

    void putActiveIpDatas(T key, List<K> ipStatsDatas);

    boolean isChanged(List<IpInfo> lastIpInfos, List<IpInfo> currIpInfos);

    List<IpInfo> getRelatedIpInfo(T key, List<IpInfo> lastIpInfos);

}
