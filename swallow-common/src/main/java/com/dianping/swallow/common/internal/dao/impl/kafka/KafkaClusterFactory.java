package com.dianping.swallow.common.internal.dao.impl.kafka;


import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.impl.AbstractClusterFactory;

/**
 * @author mengwenchao
 *
 * 2015年11月2日 下午4:03:45
 */
public class KafkaClusterFactory extends AbstractClusterFactory{
	
	
	private String KAFKA_CONFIG_FILENAME = "swallow-kafka.properties";
	
	private String kafkaConfigLionSuffix;
	
	private KafkaConfig kafkaConfig;

	
	public KafkaClusterFactory(){
		
	}
	
	public KafkaClusterFactory(String kafkaConfigLionSuffix){
		this.kafkaConfigLionSuffix = kafkaConfigLionSuffix;
	}
	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		kafkaConfig = new KafkaConfig(KAFKA_CONFIG_FILENAME, kafkaConfigLionSuffix);
	}

	@Override
	public Cluster createCluster(String address) {
		return new KafkaCluster(address, kafkaConfig);
	}

	@Override
	public boolean accepts(String url) {
		return isKafkaUrl(getTypeDesc(url));
	}

	
	private boolean isKafkaUrl(String type) {
		
		if(type != null && type.equalsIgnoreCase(KafkaCluster.schema)){
			return true;
		}
		
		return false;
	}


}
