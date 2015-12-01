package com.dianping.swallow.kafka.consumer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kafka.api.FetchRequest;
import kafka.api.PartitionFetchInfo;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.OffsetAndMetadata;
import kafka.common.OffsetMetadataAndError;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetCommitRequest;
import kafka.javaapi.OffsetCommitResponse;
import kafka.javaapi.OffsetFetchRequest;
import kafka.javaapi.OffsetFetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.JavaConversions;

import com.dianping.swallow.kafka.KafkaConsumer;
import com.dianping.swallow.kafka.KafkaMessage;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.simple.KafkaGetElemetFailException;
import com.dianping.swallow.kafka.consumer.simple.SimpleKafkaConsumerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:32:52
 */
public abstract class AbstractKafkaConsumer implements KafkaConsumer{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected List<InetSocketAddress> seedBrokers;
	
	protected String clientId;
	
	private int soTimeout = 100000;
	
	private int fetchSize = 2 * 1024 * 1024; 
	
	private int minBytes = 0;
	
	private int maxWait = 5000;
	
	private int fetchRetryCount = 3;
	
	private Map<TopicAndPartition, PartitionMetadata> partitionMap = new ConcurrentHashMap<TopicAndPartition, PartitionMetadata>();
	
	
	private int maxTotalPerKey = 100;
	private int maxIdlePerKey = 100;
	private boolean blockWhenExhausted = true;
	private int maxWaitMillis = 1000;
	
	private KeyedObjectPool<InetSocketAddress, SimpleConsumer>  consumerPool;


	public AbstractKafkaConsumer(List<InetSocketAddress> seedBrokers, String clientId, int minBytes, int soTimeout, 
			int fetchSize, int maxWait, int fetchRetryCount, int maxTotalPerKey, int maxIdlePerKey, boolean blockWhenExhausted, int maxWaitMillis) {
		
		this.seedBrokers = seedBrokers;
		this.clientId = clientId;
		this.minBytes = minBytes;
		this.maxWait = maxWait;
		this.fetchRetryCount = fetchRetryCount;
		this.soTimeout = soTimeout;
		this.fetchSize = fetchSize;
		
		//for object pool
		this.maxTotalPerKey = maxTotalPerKey;
		this.maxIdlePerKey = maxIdlePerKey;
		this.blockWhenExhausted = blockWhenExhausted;
		this.maxWaitMillis = maxWaitMillis;
		
		consumerPool = createConsumerPool();
		
	}
	
	private KeyedObjectPool<InetSocketAddress, SimpleConsumer> createConsumerPool() {
		
		GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
		
		config.setMaxIdlePerKey(maxIdlePerKey);
		config.setMaxTotalPerKey(maxTotalPerKey);
		config.setBlockWhenExhausted(blockWhenExhausted);
		config.setMaxWaitMillis(maxWaitMillis);
		
		if(logger.isInfoEnabled()){
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				logger.info("[createConsumerPool]" + objectMapper.writeValueAsString(config));
			} catch (JsonProcessingException e) {
				logger.info("[createConsumerPool]", e);
			}
		}
		
		KeyedObjectPool<InetSocketAddress, SimpleConsumer> consumerPool =  new GenericKeyedObjectPool<InetSocketAddress, SimpleConsumer>(
				new SimpleKafkaConsumerFactory(soTimeout, fetchSize, clientId), config);
		return consumerPool;
	}

	@Override
	public Long getMaxMessageId(TopicAndPartition tp) {
		
		SimpleConsumer simpleConsumer = null;
		try{
			simpleConsumer = getProperConsumer(tp);
			return getMaxMessageId(simpleConsumer, tp) - 1;
		}finally{
			returnConsumer(simpleConsumer);
		}
	}

	@Override
	public Long getMinMessageId(TopicAndPartition tp) {
		
		SimpleConsumer simpleConsumer = null;
		try{
			simpleConsumer = getProperConsumer(tp);
			return getMiniMessageId(simpleConsumer, tp);
		}finally{
			returnConsumer(simpleConsumer);
		}
	}
	
	
	private SimpleConsumer getProperConsumer(TopicAndPartition tp){
		
		Broker broker = getProperBroker(tp);
		InetSocketAddress address = new InetSocketAddress(broker.host(), broker.port());
		SimpleConsumer consumer = getConsumer(address);
		return consumer;
	}
	
	@Override
	public void saveAck(TopicAndPartition tp, String groupId, Long ack) {
	
		SimpleConsumer simpleConsumer = null;
		
		try{
			simpleConsumer = getProperConsumer(tp);
			
			Map<kafka.common.TopicAndPartition, OffsetAndMetadata> requestInfo = new HashMap<kafka.common.TopicAndPartition, OffsetAndMetadata>();
			requestInfo.put(tp.toKafka(), new OffsetAndMetadata(ack, "ack", System.currentTimeMillis()));
			
			 
			OffsetCommitRequest request = new OffsetCommitRequest(groupId, requestInfo, getCorRelationId(), clientId);
					
			OffsetCommitResponse response = simpleConsumer.commitOffsets(request);
			
			if(response.hasError()){
				logger.error("[saveAck]" + tp + "," + groupId + "," + ack, ErrorMapping.exceptionFor(response.errorCode(tp.toKafka())));
			}
		}finally{
			returnConsumer(simpleConsumer);
		}
	}

	@Override
	public Long getAck(TopicAndPartition tp, String groupId) {
		
		SimpleConsumer simpleConsumer = null;
		try{
			simpleConsumer = getProperConsumer(tp);
			
			List<kafka.common.TopicAndPartition> requestInfo = new LinkedList<kafka.common.TopicAndPartition>();
			requestInfo.add(tp.toKafka());
			
			OffsetFetchRequest request = new OffsetFetchRequest(groupId, requestInfo, getCorRelationId(), clientId);
			OffsetFetchResponse response = simpleConsumer.fetchOffsets(request);
			
			OffsetMetadataAndError offset = response.offsets().get(tp.toKafka());
			
			if(offset == null){
				return null;
			}
			return offset.offset();
		}catch(UnfoundMetaDataException e){
			logger.error("[getAck]" + tp + "," + groupId, e);
		}finally{
			returnConsumer(simpleConsumer);
		}
		
		return null;
		
	}

	
	@Override
	public List<KafkaMessage> getMessageGreatThan(TopicAndPartition tp, Long offset, int fetchSize) {
		
		FetchResponse response = null;
		
		for(int i=0; i <= fetchRetryCount; i++){
			
			SimpleConsumer simpleConsumer = null;
			try{
		
				simpleConsumer = getProperConsumer(tp);
				
				Map<kafka.common.TopicAndPartition, PartitionFetchInfo>  requestInfo = new HashMap<kafka.common.TopicAndPartition, PartitionFetchInfo>();
				requestInfo.put(tp.toKafka(), new PartitionFetchInfo(offset + 1, fetchSize));
				
				FetchRequest request = createFetchRequest(requestInfo);
				
				if(logger.isDebugEnabled()){
					logger.debug("[getMessageGreatThan][begin]" + tp + "," + offset + "," + fetchSize);
				}
				response = simpleConsumer.fetch(request);
				if(logger.isDebugEnabled()){
					logger.debug("[getMessageGreatThan][end]" + tp + "," + offset + "," + fetchSize);
				}
				
				if(!response.hasError()){
					break;
				}
				
				short errorCode = response.errorCode(tp.getTopic(), tp.getPartition());
				
				if(errorCode == ErrorMapping.OffsetOutOfRangeCode()){
					
					if(logger.isInfoEnabled()){
						logger.info("[getMessageGreatThan][offset not right]" + tp + "," + offset);
					}
					Long minId = getMiniMessageId(simpleConsumer, tp);
					if(offset < minId){
						offset = minId - 1;
					}else{
						break;
					}
				}else if(errorCode == ErrorMapping.NotLeaderForPartitionCode()){
					
					logger.warn("[getMessageGreatThan][not leader]" + tp + "," + offset);
				}else{
					logger.error("[getMessageGreatThan]" + tp + "," + offset, ErrorMapping.exceptionFor(errorCode));
				}
			}catch(Exception e){
				logger.error("[getMessageGreatThan]" + tp + "," + offset, e);
			}finally{
				returnConsumer(simpleConsumer);
			}
			
			getPartitionMetadata(tp, true);
			
		}
		
		
		List<KafkaMessage> result = new LinkedList<KafkaMessage>();
		
		if(response == null){
			return result;
		}
		
		for(MessageAndOffset message : response.messageSet(tp.getTopic(), tp.getPartition())){
			
			long currentOffset = message.offset();
			if(currentOffset <= offset){
				continue;
			}
			result.add(new KafkaMessage(tp, message.offset(), message.message().payload()));
		}
		
		return result;
	}
	
	
	@Override
	public List<KafkaMessage> getMessageGreatThan(TopicAndPartition tp, Long offset) {
		return getMessageGreatThan(tp, offset, fetchSize);
	}

	
	private Long getMaxMessageId(SimpleConsumer consumer, TopicAndPartition tp) {
		
		return getMessageId(consumer, kafka.api.OffsetRequest.LatestTime(), tp);
	}


	private Long getMiniMessageId(SimpleConsumer consumer, TopicAndPartition tp) {
		
		return getMessageId(consumer, kafka.api.OffsetRequest.EarliestTime(), tp);
	}
	
	private Long getMessageId(SimpleConsumer consumer, long time, TopicAndPartition tp) {
	
		Map<kafka.common.TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<kafka.common.TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(tp.toKafka(), new PartitionOffsetRequestInfo(time, 1));
		OffsetRequest request = new OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientId);
		
		modifyReplicaIdIfPossible(request);
		
		OffsetResponse response = consumer.getOffsetsBefore(request);
		
		if(response.hasError()){
			
			Short errorCode = response.errorCode(tp.getTopic(), tp.getPartition());
			logger.error("[getMessageId]" + consumer + "," + tp, ErrorMapping.exceptionFor(errorCode));
			return 0L;
		}

		long [] offsets = response.offsets(tp.getTopic(), tp.getPartition());
		if(offsets.length < 1){
			logger.error("[getMessageId][error offset]" + tp + "," + consumer + ",length:" + offsets.length);
			return 0L;
		}
		
		return offsets[0];
	}

	/**
	 * 在从slave获取offset的情况下修改replicaId
	 * @param request
	 */
	protected void modifyReplicaIdIfPossible(OffsetRequest request) {
		
	}

	private FetchRequest createFetchRequest(Map<kafka.common.TopicAndPartition, PartitionFetchInfo> requestInfo) {
		
		return new FetchRequest(FetchRequest.CurrentVersion(), getCorRelationId(), clientId, getReplicaId(), maxWait, minBytes, convert(requestInfo));
	}

	
	public <K, V> scala.collection.immutable.Map<K, V> convert(java.util.Map<K, V> m) {

		return JavaConversions.asScalaMap(m).toMap(scala.Predef.<scala.Tuple2<K, V>>conforms());
	}

	protected abstract int getReplicaId();

	private int getCorRelationId() {
		return 0;
	}

	private SimpleConsumer getConsumer(InetSocketAddress address) {
		SimpleConsumer consumer;
		try {
			consumer = consumerPool.borrowObject(address);
		} catch (Exception e) {
			throw new KafkaGetElemetFailException(address, e);
		}
		return consumer;
	}
	
	private void returnConsumer(SimpleConsumer simpleConsumer){
		
		if(simpleConsumer == null){
			logger.warn("[returnConsumer]" + simpleConsumer);
			return;
		}
		
		InetSocketAddress address = new InetSocketAddress(simpleConsumer.host(), simpleConsumer.port());
		try {
			consumerPool.returnObject(address, simpleConsumer);
		} catch (Exception e) {
			logger.error("[returnConsumer][fail]"  + simpleConsumer + "," + address);
		}
	}

	private Broker getProperBroker(TopicAndPartition tp){
		
		PartitionMetadata metaData = getPartitionMetadata(tp);
		
		if(metaData == null){
			throw new UnfoundMetaDataException(tp);
		}
		
		return selectBrokerForPartition(tp, metaData);
	}
	

	protected abstract Broker selectBrokerForPartition(TopicAndPartition tp, PartitionMetadata metaData);

	private PartitionMetadata getPartitionMetadata(TopicAndPartition tp){
		
		return getPartitionMetadata(tp, false);
	}

	private PartitionMetadata getPartitionMetadata(TopicAndPartition tp, boolean flush){
		
		PartitionMetadata returnMetadata = null;
		
		if(!flush){
			
			returnMetadata = partitionMap.get(tp);
			if(returnMetadata != null){
				return returnMetadata;
			}
		}
		
		returnMetadata = getPartitionMetadata_(tp);
		
		if(returnMetadata != null){
			partitionMap.put(tp, returnMetadata);
		}
		
		return returnMetadata;

	}
	
	private PartitionMetadata getPartitionMetadata_(TopicAndPartition tp) {
		
		for(InetSocketAddress broker : seedBrokers){
			
			SimpleConsumer simpleConsumer = null;
			try {
				simpleConsumer = getConsumer(broker);
				List<String> topics = Collections.singletonList(tp.getTopic());
				TopicMetadataRequest req = new TopicMetadataRequest(topics);
				kafka.javaapi.TopicMetadataResponse resp = simpleConsumer.send(req);

				List<TopicMetadata> metaData = resp.topicsMetadata();
				for (TopicMetadata item : metaData) {
					for (PartitionMetadata part : item.partitionsMetadata()) {
						if (part.partitionId() == tp.getPartition()) {
							if(logger.isInfoEnabled()){
								logger.info("[getPartitionMetadata_]" + tp + "," + part);
							}
							return part;
						}
					}
				}
			} catch (Exception e) {
				logger.error("[error]" + simpleConsumer, e);
			} finally {
				returnConsumer(simpleConsumer);
			}
		}
		
		return null;
	}

	public static List<InetSocketAddress> getSeedBrokers(String kafkaAddress) {
		
		
		List<InetSocketAddress> result = new LinkedList<InetSocketAddress>();
		
		for(String address : kafkaAddress.split("\\s*,\\s*")){
			
			String []ipport = address.split("\\s*:\\s*");
			if(ipport.length != 2){
				throw new IllegalArgumentException("wrong ip address:" + address);
			}
			result.add(new InetSocketAddress(ipport[0], Integer.parseInt(ipport[1])));
		}
		
		return result;
	}

}
