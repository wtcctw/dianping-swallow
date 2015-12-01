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


	public SimpleKafkaConsumer(String kafkaAddress, String clientId, int minBytes, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount, int maxTotalPerKey, int maxIdlePerKey, boolean blockWhenExhausted, int maxWaitMillis) {
		this(getSeedBrokers(kafkaAddress), clientId, minBytes, soTimeout, fetchSize, maxWait, fetchRetryCount, maxWaitMillis, maxWaitMillis, blockWhenExhausted, maxWaitMillis);
	}
	

	public SimpleKafkaConsumer(List<InetSocketAddress> seedBrokers, String clientId, int minBytes, int soTimeout, int fetchSize,
			int maxWait, int fetchRetryCount, int maxTotalPerKey, int maxIdlePerKey, boolean blockWhenExhausted, int maxWaitMillis) {
		super(seedBrokers, clientId, minBytes, soTimeout, fetchSize, maxWait, fetchRetryCount, maxWaitMillis, maxWaitMillis, blockWhenExhausted, maxWaitMillis);
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
