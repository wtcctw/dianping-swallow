package com.dianping.swallow.web.monitor;

import java.util.NavigableMap;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.StatisData;

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

    //Object getValue(CasKeys keys, StatisType type);

    Set<String> getKeys(CasKeys keys);

    //Object getValue(CasKeys keys);

    NavigableMap<Long, StatisData> getLastStatisValue(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getFirstStatisValue(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getStatisValue(CasKeys keys, StatisType type, Long startKey, Long endKey);

    NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type);

    NavigableMap<Long, Statisable.QpxData> getQpsValue(CasKeys keys, StatisType type);

}
