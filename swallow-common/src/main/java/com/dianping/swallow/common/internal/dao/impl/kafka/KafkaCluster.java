package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.impl.AbstractCluster;
import com.dianping.swallow.common.internal.dao.impl.kafka.serialization.SwallowMessageSerializer;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.kafka.zk.ZkUtils;

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
	
	private Map<TOPIC_TYPE, KafkaProducer<String, SwallowMessage>>  producers = new HashMap<TOPIC_TYPE, KafkaProducer<String,SwallowMessage>>();   

	public KafkaCluster(String address) {
		super(address);
	}

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		zkUtils = new ZkUtils(getAddressString(getSeeds()));

		
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
	
	private KafkaProducer<String, SwallowMessage> createKafkaProducer(String address, String acks) {
		
		Properties props = new Properties();

		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SwallowMessageSerializer.class);
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
	
	
	public KafkaProducer<String, SwallowMessage>  getProducer(String topicName){
		
		TopicConfig topicConfig = swallowConfig.getTopicConfig(topicName);
		TOPIC_TYPE topicType = TOPIC_TYPE.DURABLE_FIRST;
		
		if(topicConfig != null && topicConfig.getTopicType() != null){
			topicType = topicConfig.getTopicType();
		}
		
		return producers.get(topicType);
	}
}
