package com.dianping.swallow.web.monitor;



import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface MonitorDataRetriever extends Retriever{

	void add(MonitorData monitorData);
	
	String getDebugInfo();
}
