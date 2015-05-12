package com.dianping.swallow.common.server.monitor.data;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTotalMap;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitor;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:44:42
 */
@Document( collection = "ConsumerMonitorData")
public class ConsumerMonitorData extends MonitorData{
	
	private ConsumerTotalMap all = new ConsumerTotalMap();

	public ConsumerMonitorData(){
		
	}
	
	public ConsumerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}
	
	@Override
	protected void doMerge(Mergeable mergeData) {
		
		if(!(mergeData instanceof ConsumerMonitorData)){
			throw new IllegalArgumentException("mergeData type not right " + mergeData.getClass());
		}
		
		ConsumerMonitorData consumerMergeData = (ConsumerMonitorData) mergeData;
		all.merge(consumerMergeData.all);
	}
	
	
	@Override
	protected Mergeable getTopic(KeyMergeable merge, String topic) {
		
		ConsumerMonitorData cmd = (ConsumerMonitorData) merge;
		return cmd.getTopic(topic);
	}

	@Override
	protected Mergeable getTopic(String topic) {
		
		return MapUtil.getOrCreate(all, topic, ConsumerTopicData.class);
	}
	
	public void addSendData(final ConsumerInfo consumerInfo, final String consumerIp, final SwallowMessage message){
		
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.sendMessage(consumerInfo.getConsumerId(), consumerIp, message);
						
	}

	public void addAckData(final ConsumerInfo consumerInfo, final String consumerIp, final SwallowMessage message){

		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.ackMessage(consumerInfo.getConsumerId(), consumerIp, message);
	}
	
	
	public void removeConsumer(ConsumerInfo consumerInfo){
		ConsumerTopicData consumerTopicData = getConsumerTopicData(consumerInfo.getDest().getName());
		consumerTopicData.removeConsumer(consumerInfo);
		
	}

	private ConsumerTopicData getConsumerTopicData(String topicName) {
		
		ConsumerTopicData consumerTopicData;
		
		synchronized (topicName.intern()) {
			
			consumerTopicData = all.get(topicName);
			if(consumerTopicData == null){
				consumerTopicData = new ConsumerTopicData();
				all.put(topicName, consumerTopicData);
			}
		}
		return consumerTopicData;
	}


	@Override
	public boolean equals(Object obj) {
		
		if(!super.equals(obj)){
			return false;
		}
		if(!(obj instanceof ConsumerMonitorData)){
			return false;
		}
		
		ConsumerMonitorData cmp = (ConsumerMonitorData) obj;
		return cmp.all.equals(all);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash = hash*31 + super.hashCode();
		hash = hash*31 + all.hashCode();
		return hash;
	}

	@Override
	protected TotalMap<?> getTopicData(String topic) {
		
		return all.get(topic);
	}

	@Override
	protected void visitAllTopic(MonitorVisitor mv) {
		
		for(String topic : all.keySet()){
			mv.visit(topic, all.get(topic));
		}
	}

	@Override
	public void buildTotal() {
		all.buildTotal();
	}

	@Override
	public Object getTotal() {

		return all.getTotal();
	}

	@Override
	public Set<String> getTopics() {
		return all.keySet();
	}

}
