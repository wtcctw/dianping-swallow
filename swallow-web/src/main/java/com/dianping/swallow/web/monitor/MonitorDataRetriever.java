package com.dianping.swallow.web.monitor;

import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午10:38:07
 */
public interface MonitorDataRetriever extends Retriever {

	void add(MonitorData monitorData);

	String getDebugInfo(String server);

	void registerListener(MonitorDataListener listener);

	Set<String> getKeys(CasKeys keys, StatisType type);

	Object getValue(CasKeys keys, StatisType type);

	Set<String> getKeys(CasKeys keys);

	Object getValue(CasKeys keys);

}
