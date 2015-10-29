package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.stats.AbstractIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;

import java.util.List;
import java.util.Set;

/**
 * @author qi.yin
 *         2015/10/27  上午11:07.
 */
public interface IpStatusMonitor<T,K extends AbstractIpStatsData> {

    void putActiveIpDatas(T key, List<K> ipStatsDatas);

    boolean isChanged(T key, List<IpInfo> currIpInfos);

    boolean isNeedLoaded(T key);

    void setLastIpInfos(T key,List<IpInfo> lastIpInfos);

    List<IpInfo> getLastIpInfos(T key);

    List<IpInfo> getRelatedIpInfo(T key);

}
