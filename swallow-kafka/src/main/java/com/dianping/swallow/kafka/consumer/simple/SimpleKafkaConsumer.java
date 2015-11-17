package com.dianping.swallow.kafka.consumer.simple;

import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.AbstractKafkaConsumer;

import kafka.api.Request;
import kafka.cluster.Broker;
import kafka.javaapi.PartitionMetadata;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:35:18
 */
public class SimpleKafkaConsumer extends AbstractKafkaConsumer{


	public SimpleKafkaConsumer(String kafkaAddress, String clientId, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount) {
		this(getSeedBrokers(kafkaAddress), clientId, soTimeout, fetchSize, maxWait, fetchRetryCount);
	}
	

	public SimpleKafkaConsumer(List<InetSocketAddress> seedBrokers, String clientId, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount) {
		super(seedBrokers, clientId, soTimeout, fetchSize, maxWait, fetchRetryCount);
	}

	@Override
	protected Broker selectBrokerForPartition(TopicAndPartition tp, PartitionMetadata metaData) {
		
		return metaData.leader();
	}

	@Override
	protected int getReplicaId() {
		return Request.OrdinaryConsumerId();
	}
	
}
