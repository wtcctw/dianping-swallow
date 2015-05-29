package com.dianping.swallow.common.server.monitor.collector;


import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.lifecycle.SelfManagement;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:51:05
 */
public class DefaultConsumerCollector extends AbstractCollector implements ConsumerCollector, SelfManagement{
	
	
	private ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
	
	@Override
	public void sendMessage(final ConsumerInfo consumerInfo, final String consumerIpPort, final SwallowMessage message) {
		
		actionWrapper.doAction(new AbstractMonitorDataAction(consumerInfo.toString(), consumerIpPort, message.getMessageId()) {
			
			@Override
			public void doAction() throws SwallowException {
				
				String topic = consumerInfo.getDest().getName();
				
				if(isExclude(topic)){
					return;
				}
				consumerMonitorData.addSendData(consumerInfo, IPUtil.getIp(consumerIpPort), message);
			}
		});
	}


	@Override
	public void ackMessage(final ConsumerInfo consumerInfo, final String consumerIpPort, final SwallowMessage message) {

		actionWrapper.doAction(new AbstractMonitorDataAction(consumerInfo.toString(), consumerIpPort, message.getMessageId()) {
			
			@Override
			public void doAction() throws SwallowException {
				
				String topic = consumerInfo.getDest().getName();
				
				if(isExclude(topic)){
					return;
				}
				consumerMonitorData.addAckData(consumerInfo, IPUtil.getIp(consumerIpPort), message);
			}
		});
		
	}

	@Override
	public void removeConsumer(final ConsumerInfo consumerInfo) {
		if(consumerInfo == null){
			logger.warn("[remove][consumerInfo == null]");
			return;
		}
		
		actionWrapper.doAction(new AbstractMonitorDataAction(consumerInfo.toString(), null) {
			
			@Override
			public void doAction() throws SwallowException {
				if(logger.isInfoEnabled()){
					logger.info("[remove][remove consumerInfo]" + consumerInfo);
				}
				consumerMonitorData.removeConsumer(consumerInfo);
			}
		});
		
	}

	@Override
	protected MonitorData getMonitorData() {
		consumerMonitorData.buildTotal();
		return consumerMonitorData;
	}

	@Override
	protected String getServerType() {
		return "consumer";
	}

}
