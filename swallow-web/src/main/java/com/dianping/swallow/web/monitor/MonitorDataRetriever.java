package com.dianping.swallow.web.monitor;

import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface MonitorDataRetriever {

	void add(MonitorData monitorData);
	
	int getKeepInMemoryHour();
	
	Set<String>  getTopics(long start, long end);

	Set<String>  getTopics();
	
}
