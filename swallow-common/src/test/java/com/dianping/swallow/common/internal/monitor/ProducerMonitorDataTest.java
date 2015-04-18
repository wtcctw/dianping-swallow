package com.dianping.swallow.common.internal.monitor;


import com.dianping.swallow.common.internal.monitor.data.MonitorData;
import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;
import com.dianping.swallow.common.internal.util.IPUtil;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public class ProducerMonitorDataTest extends AbstractMonitorDataTest{

	
	
	
	protected Class<? extends MonitorData> getMonitorClass() {
		
		return ProducerMonitorData.class;
	}

	protected String getJsonString() {
		
		MonitorData monitorData = createMonitorData();
		
		return monitorData.jsonSerialize();
	}

	
	protected MonitorData createMonitorData() {
		
		ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
		producerMonitorData.addData("topic", ip, 1L, System.currentTimeMillis() - 100, System.currentTimeMillis());
		
		return producerMonitorData;
		
	}

	@Override
	protected String getUrl() {
		
		return "http://127.0.0.1:8080/api/stats/producer";
	}

}
