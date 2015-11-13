package com.dianping.swallow.consumer.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.heartbeat.DefaultHeartBeatSender;
import com.dianping.swallow.common.internal.heartbeat.HeartBeatSender;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;
import com.dianping.swallow.consumer.internal.ConsumerImpl;

public final class ConsumerFactoryImpl implements ConsumerFactory {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String LION_CONFIG_FILENAME = "swallow-consumerclient-lion.properties";

	private static final String TOPICNAME_DEFAULT = "default";
	private final static String LION_KEY_CONSUMER_SERVER_URI = "swallow.consumer.consumerServerURI";
	private Map<String, List<InetSocketAddress>> topicName2Address = new HashMap<String, List<InetSocketAddress>>();
	private static ConsumerFactoryImpl instance = new ConsumerFactoryImpl();
	private HeartBeatSender heartBeatSender = new DefaultHeartBeatSender();

	static {
		SwallowHelper.initialize();
	}

	private ConsumerFactoryImpl() {
		getSwallowCAddress();
	}

	public static ConsumerFactory getInstance() {
		return instance;
	}

	@Override
	public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {
		if (topicName2Address.get(dest.getName()) != null) {
			return new ConsumerImpl(dest, consumerId, config, topicName2Address.get(dest.getName()).get(0),
					topicName2Address.get(dest.getName()).get(1), heartBeatSender);
		} else {
			return new ConsumerImpl(dest, consumerId, config, topicName2Address.get(TOPICNAME_DEFAULT).get(0),
					topicName2Address.get(TOPICNAME_DEFAULT).get(1), heartBeatSender);
		}

	}

	@Override
	public Consumer createConsumer(Destination dest, String consumerId) {
		return createConsumer(dest, consumerId, new ConsumerConfig());
	}

	@Override
	public Consumer createConsumer(Destination dest, ConsumerConfig config) {
		return createConsumer(dest, null, config);
	}

	@Override
	public Consumer createConsumer(Destination dest) {
		return createConsumer(dest, new ConsumerConfig());
	}

	private void getSwallowCAddress() {
		DynamicConfig dynamicConfig = new LionDynamicConfig(LION_CONFIG_FILENAME);
		String lionValue = dynamicConfig.get(LION_KEY_CONSUMER_SERVER_URI);
		lionValue2Map(lionValue);
	}

	/**
	 * 
	 * @param lionValue
	 *            swallow.consumer.consumerServerURI=default=127.0.0.1:8081,
	 *            127.0
	 *            .0.1:8082;feed,topicForUnitTest=127.0.0.1:8083,127.0.0.1:8084
	 * @return
	 */
	protected void lionValue2Map(String lionValue) {

		if (logger.isInfoEnabled()) {
			logger.info("[lionValue2Map][config]" + lionValue);
		}

		for (String topicNameToAddress : lionValue.split(getSplitWithSpace(";"))) {
			String[] splits = topicNameToAddress.split("=");
			if (splits.length != 2) {
				throw new IllegalStateException("wrong swallow.consumer.consumerServerURI:" + topicNameToAddress);
			}
			String topicNames = splits[0].trim();
			String swallowCAddress = splits[1].trim();

			for (String topicName : topicNames.split(getSplitWithSpace(","))) {
				string2Map(topicName.trim(), swallowCAddress);
			}
		}
	}
	
	private String getSplitWithSpace(String split){
		return "\\s*" + split + "\\s*";
	}

	private void string2Map(String topicName, String swallowCAddress) {

		String[] ipAndPorts = swallowCAddress.split(getSplitWithSpace(","));

		if (ipAndPorts.length != 2) {
			throw new IllegalArgumentException("bad swallowAddress:" + swallowCAddress);
		}

		String []masterConfig = ipAndPorts[0].split(getSplitWithSpace(":")); 
		String masterIp = masterConfig[0];
		int masterPort = Integer.parseInt(masterConfig[1]);

		String []slaveConfig = ipAndPorts[1].split(getSplitWithSpace(":")); 
		String slaveIp = slaveConfig[0];
		int slavePort = Integer.parseInt(slaveConfig[1]);
		
		List<InetSocketAddress> tempAddress = new ArrayList<InetSocketAddress>();
		tempAddress.add(new InetSocketAddress(masterIp, masterPort));
		tempAddress.add(new InetSocketAddress(slaveIp, slavePort));
		if (logger.isInfoEnabled()) {
			logger.info("[string2Map][topic, address]" + topicName + "," + tempAddress);
		}
		topicName2Address.put(topicName, tempAddress);
	}


	/**
	 * for unittest
	 * @param topic
	 * @return
	 */
	protected List<InetSocketAddress> getTopicAddress(String topic){
		
	   return topicName2Address.get(topic); 
   }

	public void setHeartBeatSender(HeartBeatSender heartBeatSender) {
		this.heartBeatSender = heartBeatSender;
	}

}
