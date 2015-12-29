package com.dianping.swallow.consumer.impl;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.heartbeat.DefaultHeartBeatSender;
import com.dianping.swallow.common.internal.heartbeat.HeartBeatSender;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservable;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.AbstractConsumerFactory;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;
import com.dianping.swallow.consumer.internal.ConsumerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author qi.yin
 *         2015/12/15  上午11:11.
 */
public class ConsumerFactoryImpl extends AbstractConsumerFactory implements ConsumerFactory, ConfigChangeListener {

    private static final String LION_CONFIG_FILENAME = "swallow-consumerclient-lion.properties";

    private static final String TOPICNAME_DEFAULT = "default";
    private final static String LION_KEY_CONSUMER_SERVER_URI = "swallow.consumer.consumerServerURI";
    private Map<String, List<InetSocketAddress>> topicName2Address = new HashMap<String, List<InetSocketAddress>>();
    private static ConsumerFactoryImpl instance = new ConsumerFactoryImpl();
    private HeartBeatSender heartBeatSender = new DefaultHeartBeatSender();

    private ConsumerFactoryImpl() {
        getSwallowCAddress();
    }

    public static ConsumerFactory getInstance() {
        return instance;
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {

        List<InetSocketAddress> addresses = getOrDefaultTopicAddress(dest.getName());
        Consumer consumer =  new ConsumerImpl(dest, consumerId, config, addresses.get(0), addresses.get(1), heartBeatSender);
        addObserver(consumer);
        return consumer;

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
        dynamicConfig.addConfigChangeListener(this);
        topicName2Address = lionValue2Map(lionValue);
    }

    /**
     *
     * @param lionValue
     *            swallow.consumer.consumerServerURI=default=127.0.0.1:8081,
     *            127.0
     *            .0.1:8082;feed,topicForUnitTest=127.0.0.1:8083,127.0.0.1:8084
     * @return
     * @return
     */
    protected Map<String, List<InetSocketAddress>> lionValue2Map(String lionValue) {

        if (logger.isDebugEnabled()) {
            logger.debug("[lionValue2Map][config]" + lionValue);
        }

        Map<String, List<InetSocketAddress>> topicName2Address = new HashMap<String, List<InetSocketAddress>>();


        for (String topicNameToAddress : lionValue.split(getSplitWithSpace(";"))) {
        	
        	if(StringUtils.isEmpty(topicNameToAddress)){
        		continue;
        	}
        	
            String[] splits = topicNameToAddress.split("=");
            if (splits.length != 2) {
                throw new IllegalStateException("wrong swallow.consumer.consumerServerURI:" + topicNameToAddress);
            }
            String topicNames = splits[0].trim();
            String swallowCAddress = splits[1].trim();

            List<InetSocketAddress>  address =  string2SocketAddress(swallowCAddress);

            for (String topicName : topicNames.split(getSplitWithSpace(","))) {
                topicName2Address.put(topicName.trim(), address);
            }
        }

        return topicName2Address;
    }

    private String getSplitWithSpace(String split){
        return "\\s*" + split + "\\s*";
    }

    private List<InetSocketAddress> string2SocketAddress(String swallowCAddress) {

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

        return tempAddress;

    }


    /**
     * for unittest
     * @param topic
     * @return
     */
    @Override
    public List<InetSocketAddress> getTopicAddress(String topic){

        List<InetSocketAddress> addresses = topicName2Address.get(topic);
        if(addresses == null){
            return null;
        }

        return new LinkedList<InetSocketAddress>(addresses);
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic) {

        List<InetSocketAddress> addresses = getTopicAddress(topic);
        if(addresses == null){
            addresses = getTopicAddress(TOPICNAME_DEFAULT);
        }
        return addresses;
    }

    public void setHeartBeatSender(HeartBeatSender heartBeatSender) {
        this.heartBeatSender = heartBeatSender;
    }

    @Override
    public void onConfigChange(String key, String value) {

        if(LION_KEY_CONSUMER_SERVER_URI.equals(key)){

            topicName2Address = lionValue2Map(value);
            updateObservers(null);
        }
    }

}
