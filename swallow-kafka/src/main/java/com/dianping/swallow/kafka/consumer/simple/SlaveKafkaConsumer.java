package com.dianping.swallow.kafka.consumer.simple;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.AbstractKafkaConsumer;
import com.dianping.swallow.kafka.exception.KafkaRuntimeException;

import kafka.api.Request;
import kafka.cluster.Broker;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.PartitionMetadata;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:35:18
 */
public class SlaveKafkaConsumer extends AbstractKafkaConsumer{


	public SlaveKafkaConsumer(String seedBrokers, String clientId, int minBytes, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount) {
		super(getSeedBrokers(seedBrokers), clientId, minBytes, soTimeout, fetchSize, maxWait, fetchRetryCount);
	}

	
	public SlaveKafkaConsumer(List<InetSocketAddress> seedBrokers, String clientId, int minBytes, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount) {
		super(seedBrokers, clientId, soTimeout, minBytes, fetchSize, maxWait, fetchRetryCount);
	}

	@Override
	protected Broker selectBrokerForPartition(TopicAndPartition tp, PartitionMetadata metaData) {
		
		
		for(Broker broker : metaData.isr()){
			if(!broker.equals(metaData.leader())){
				if(logger.isDebugEnabled()){
					logger.debug("[selectBrokerForPartition]" + tp + "," + broker);
				}
				return broker;
			}
		}
		
		throw new KafkaRuntimeException("unfound slave node for " + tp + ", metaData:" + metaData);
	}

	@Override
	protected int getReplicaId() {
		return Request.DebuggingConsumerId();
	}

	private void setReplicaId(OffsetRequest request, int debuggingConsumerId) {
		
		String underlying = "underlying", replicaId = "replicaId";
		
		try {
			
			Field underlyingField = OffsetRequest.class.getDeclaredField(underlying);
			underlyingField.setAccessible(true);
			kafka.api.OffsetRequest kafkaRequest = (kafka.api.OffsetRequest) underlyingField.get(request);

			
			Field replicaIdField = kafka.api.OffsetRequest.class.getDeclaredField(replicaId);
			replicaIdField.setAccessible(true);
			
			replicaIdField.set(kafkaRequest, debuggingConsumerId);
			
			
		} catch (Exception e){
			logger.error("[setReplicaId]", e);
		}
	}

	protected void modifyReplicaIdIfPossible(OffsetRequest request) {
		
		setReplicaId(request, getReplicaId());
	}
}
