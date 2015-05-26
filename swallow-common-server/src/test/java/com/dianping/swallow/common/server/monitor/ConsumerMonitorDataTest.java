package com.dianping.swallow.common.server.monitor;



import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午10:24:34
 */
public class ConsumerMonitorDataTest extends AbstractMonitorDataTest{

	protected Class<? extends MonitorData> getMonitorClass() {
		
		return ConsumerMonitorData.class;
	}

	protected MonitorData createMonitorData() {
		
		ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
		
		ConsumerInfo consumerInfo = createConsumerInfo();
		
		SwallowMessage message = createMessage();
		consumerMonitorData.addSendData(consumerInfo, ip, message);
		consumerMonitorData.addAckData(consumerInfo, ip, message);
		
		return consumerMonitorData;
		
	}

	private ConsumerInfo createConsumerInfo() {
		
		ConsumerInfo consumerInfo = new ConsumerInfo("consumerInfo", Destination.topic("mytopic"), ConsumerType.DURABLE_AT_LEAST_ONCE);
		return consumerInfo;
	}

	@Override
	protected String getUrl() {
		
		return "http://localhost:8080/api/stats/consumer";
	}

	@Override
	protected void checkTotal(MonitorData monitorData) {
		
	}

	@Override
	protected void addMessages(MonitorData monitorData) {
		
	}

}
