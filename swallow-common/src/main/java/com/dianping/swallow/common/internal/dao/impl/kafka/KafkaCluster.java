package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractCluster;
import com.dianping.swallow.common.internal.dao.impl.kafka.serialization.SwallowMessageDeserializer;
import com.dianping.swallow.common.internal.dao.impl.kafka.serialization.SwallowMessageSerializer;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.kafka.KafkaConsumer;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.simple.SimpleKafkaConsumer;
import com.dianping.swallow.kafka.consumer.simple.SlaveKafkaConsumer;
import com.dianping.swallow.kafka.zookeeper.ZkUtils;

/**
 * 
 * address: kafka://ip:port,ip:port<br/>提供zk地址
 * @author mengwenchao
 *
 * 2015年11月1日 下午10:08:56
 */
public class KafkaCluster extends AbstractCluster{
	
	public static String schema = "kafka://";
	
	private ZkUtils zkUtils;
	
	public static final String ACKS_EFFICIENCY = "1";
	
	public static final String ACKS_DURABLE = "-1";
	
	private KafkaConfig kafkaConfig;
	
	private Map<TOPIC_TYPE, KafkaProducer<String, SwallowMessage>>  producers = new HashMap<TOPIC_TYPE, KafkaProducer<String,SwallowMessage>>();
	
	private KafkaConsumer kafkaConsumer;

	public KafkaCluster(String address, KafkaConfig kafkaConfig) {
		super(address);
		this.kafkaConfig = kafkaConfig;
	}

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		zkUtils = new ZkUtils(getAddressString(getSeeds()));

		if(kafkaConfig.isReadFromMaster()){
			kafkaConsumer = new SimpleKafkaConsumer(allKafkaServers(), getClientId(), kafkaConfig.getMinBytes(), 
					kafkaConfig.getSoTimeout() , kafkaConfig.getFetchSize(), kafkaConfig.getMaxWait(), kafkaConfig.getFetchRetryCount(), 
					kafkaConfig.getMaxConnectionPerHost(), kafkaConfig.getMaxIdlePerHost(), kafkaConfig.isBlockWhenExhausted(), kafkaConfig.getMaxWaitMillis());
		}else{
			kafkaConsumer = new SlaveKafkaConsumer(allKafkaServers(), getClientId(), kafkaConfig.getMinBytes(),
					kafkaConfig.getSoTimeout(), kafkaConfig.getFetchSize(), kafkaConfig.getMaxWait(), kafkaConfig.getFetchRetryCount(),
					kafkaConfig.getMaxConnectionPerHost(), kafkaConfig.getMaxIdlePerHost(), kafkaConfig.isBlockWhenExhausted(), kafkaConfig.getMaxWaitMillis());
			
		}
		
		String address = getAddressString(allKafkaServers());
		
		for(TOPIC_TYPE type : TOPIC_TYPE.values()){
			
			KafkaProducer<String, SwallowMessage> producer = null;
			switch(type){
				case EFFICIENCY_FIRST:
					producer = createKafkaProducer(address, ACKS_EFFICIENCY);
					break;
				case DURABLE_FIRST:
				default:
					producer = createKafkaProducer(address, ACKS_DURABLE);
					break;
			}
			producers.put(type, producer);
		}
		
	}
	
	private String getClientId() {
		
		String ip = IPUtil.getFirstNoLoopbackIP4Address();
		return "client-" + ip;
	}

	private KafkaProducer<String, SwallowMessage> createKafkaProducer(String address, String acks) {
		
		Properties props = new Properties();

		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SwallowMessageSerializer.class);
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaConfig.getZip());
		props.put(ProducerConfig.ACKS_CONFIG, acks);

		KafkaProducer<String, SwallowMessage> producer = new KafkaProducer<String, SwallowMessage>(props);
		
		return producer;
	}

	@Override
	public List<InetSocketAddress> allServers() {
		
		return allKafkaServers();
	}
	
	
	protected List<InetSocketAddress> allKafkaServers() {
		
		return zkUtils.getAllBrokersInCluster();
	}
	
	@Override
	protected String getSchema() {
		return schema;
	}
	
	
	public Deserializer<SwallowMessage> getDeserializer(String topicName){
		
		return new SwallowMessageDeserializer();
	}
	
	public KafkaConsumer getConsumer(String topic){
		
		return kafkaConsumer;
	}
	
	public void saveBackupAck(TopicAndPartition tp, String groupId, Long ack){
		
		zkUtils.saveBackupAck(tp, groupId, ack);
		
	}

	public Long getBackupAck(TopicAndPartition tp, String groupId){
		return zkUtils.getBackupAck(tp, groupId);
	}
	
	
	public KafkaProducer<String, SwallowMessage>  getProducer(String topicName){
		
		TopicConfig topicConfig = swallowServerConfig.getTopicConfig(topicName);
		TOPIC_TYPE topicType = TOPIC_TYPE.DURABLE_FIRST;
		
		if(topicConfig != null && topicConfig.getTopicType() != null){
			topicType = topicConfig.getTopicType();
		}
		
		return producers.get(topicType);
	}

	@Override
	public MessageDAO<?> createMessageDao() {
		return new KafkaMessageDao(this);
	}
}
