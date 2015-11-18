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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.JavaConversions;

import com.dianping.swallow.kafka.KafkaConsumer;
import com.dianping.swallow.kafka.KafkaMessage;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.exception.KafkaRuntimeException;

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
	
	private int minBytes = 10;
	
	private int maxWait = 5000;
	
	private int fetchRetryCount = 3;
	
	private Map<TopicAndPartition, PartitionMetadata> partitionMap = new ConcurrentHashMap<TopicAndPartition, PartitionMetadata>();
	
	private Map<InetSocketAddress, SimpleConsumer> allConsumers = new HashMap<InetSocketAddress, SimpleConsumer>(); 


	public AbstractKafkaConsumer(List<InetSocketAddress> seedBrokers, String clientId, int soTimeout, int fetchSize, int maxWait, int fetchRetryCount) {
		
		this.seedBrokers = seedBrokers;
		this.clientId = clientId;
		this.maxWait = maxWait;
		this.fetchRetryCount = fetchRetryCount;
		this.soTimeout = soTimeout;
		this.fetchSize = fetchSize;
		
		initConsumers();
	}
	
	@Override
	public Long getMaxMessageId(TopicAndPartition tp) {
		
		SimpleConsumer simpleConsumer = getProperConsumer(tp);
		return getMaxMessageId(simpleConsumer, tp) - 1;
	}

	@Override
	public Long getMinMessageId(TopicAndPartition tp) {
		
		return getMiniMessageId(getProperConsumer(tp), tp);
	}
	
	
	private SimpleConsumer getProperConsumer(TopicAndPartition tp){
		
		Broker broker = getProperBroker(tp);
		SimpleConsumer consumer = getOrCreateConsumer(broker);
		return consumer;
	}
	
	@Override
	public void saveAck(TopicAndPartition tp, String groupId, Long ack) {
	
		SimpleConsumer simpleConsumer = getProperConsumer(tp);
		
		Map<kafka.common.TopicAndPartition, OffsetAndMetadata> requestInfo = new HashMap<kafka.common.TopicAndPartition, OffsetAndMetadata>();
		requestInfo.put(tp.toKafka(), new OffsetAndMetadata(ack, "ack", System.currentTimeMillis()));
		
		 
		OffsetCommitRequest request = new OffsetCommitRequest(groupId, requestInfo, getCorRelationId(), clientId);
				
		OffsetCommitResponse response = simpleConsumer.commitOffsets(request);
		
		if(response.hasError()){
			logger.error("[saveAck]" + tp + "," + groupId + "," + ack, ErrorMapping.exceptionFor(response.errorCode(tp.toKafka())));
		}
	}

	@Override
	public Long getAck(TopicAndPartition tp, String groupId) {
		
		SimpleConsumer simpleConsumer = getProperConsumer(tp);
		
		List<kafka.common.TopicAndPartition> requestInfo = new LinkedList<kafka.common.TopicAndPartition>();
		requestInfo.add(tp.toKafka());
		
		OffsetFetchRequest request = new OffsetFetchRequest(groupId, requestInfo, getCorRelationId(), clientId);
		OffsetFetchResponse response = simpleConsumer.fetchOffsets(request);
		
		OffsetMetadataAndError offset = response.offsets().get(tp.toKafka());
		
		if(offset == null || offset.offset() == OffsetAndMetadata.InvalidOffset()){
			return null;
		}
		
		return offset.offset();
	}

	
	@Override
	public List<KafkaMessage> getMessageGreatThan(TopicAndPartition tp, Long offset, int fetchSize) {
		
		FetchResponse response = null;
		
		for(int i=0; i <= fetchRetryCount; i++){
			
			try{
		
				SimpleConsumer consumer = getProperConsumer(tp);
				
				Map<kafka.common.TopicAndPartition, PartitionFetchInfo>  requestInfo = new HashMap<kafka.common.TopicAndPartition, PartitionFetchInfo>();
				requestInfo.put(tp.toKafka(), new PartitionFetchInfo(offset + 1, fetchSize));
				
				
				FetchRequest request = createFetchRequest(requestInfo);
				
				response = consumer.fetch(request);
				
				if(!response.hasError()){
					break;
				}
				
				short errorCode = response.errorCode(tp.getTopic(), tp.getPartition());
				
				if(errorCode == ErrorMapping.OffsetOutOfRangeCode()){
					
					if(logger.isInfoEnabled()){
						logger.info("[getMessageGreatThan][offset not right]" + tp + "," + offset);
					}
					Long minId = getMiniMessageId(consumer, tp);
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
			result.add(new KafkaMessage(message.offset(), message.message().payload()));
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

	private SimpleConsumer getOrCreateConsumer(Broker broker) {
		
		InetSocketAddress address = new InetSocketAddress(broker.host(), broker.port());
		SimpleConsumer consumer = allConsumers.get(address);
		if(consumer != null){
			return consumer;
		}
		
		synchronized (allConsumers) {
			consumer = allConsumers.get(address);
			if(consumer == null){
				consumer = createConsumer(address);
			}
		}
		
		return consumer;
	}

	private void initConsumers() {
		
		for(InetSocketAddress seed : seedBrokers){
			
			createConsumer(seed);
		}
	}

	private SimpleConsumer createConsumer(InetSocketAddress seed) {
		
		SimpleConsumer consumer = new SimpleConsumer(seed.getHostName(), seed.getPort(), soTimeout, fetchSize, clientId);
		this.allConsumers.put(seed, consumer);
		return consumer;
	}

	private Broker getProperBroker(TopicAndPartition tp){
		
		PartitionMetadata metaData = getPartitionMetadata(tp);
		
		if(metaData == null){
			throw new KafkaRuntimeException("can not find meta data for partiton:" + tp);
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
		
		PartitionMetadata returnMetadata = null;
		loop: for (SimpleConsumer consumer : allConsumers.values()) {
			try {
				List<String> topics = Collections.singletonList(tp.getTopic());
				TopicMetadataRequest req = new TopicMetadataRequest(topics);
				kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

				List<TopicMetadata> metaData = resp.topicsMetadata();
				for (TopicMetadata item : metaData) {
					for (PartitionMetadata part : item.partitionsMetadata()) {
						if (part.partitionId() == tp.getPartition()) {
							returnMetadata = part;
							break loop;
						}
					}
				}
			} catch (Exception e) {
				logger.error("[error]" + consumer, e);
			} finally {
				if (consumer != null)
					consumer.close();
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[getPartitionMetadata_]" + tp + "," + returnMetadata);
		}
		return returnMetadata;
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
