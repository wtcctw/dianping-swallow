package com.dianping.swallow.web.monitor;

import java.util.NavigableMap;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.RetrieveType;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午10:38:07
 */
public interface MonitorDataRetriever extends Retriever {

    void add(MonitorData monitorData);

    String getDebugInfo(String server);

    void registerListener(MonitorDataListener listener);

    Set<String> getKeys(CasKeys keys, StatisType type);

    Set<String> getKeys(CasKeys keys);

    NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type);

    NavigableMap<Long, Statisable.QpxData> getQpsValue(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getMinData(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getMaxData(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getMoreThanData(CasKeys keys, StatisType type, Long startKey);

    NavigableMap<Long, StatisData> getLessThanData(CasKeys keys, StatisType type, Long stopKey);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType, Long startKey, Long stopKey);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey);

}
