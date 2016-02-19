package com.dianping.swallow.web.monitor.jmx.broker;

import com.dianping.swallow.web.monitor.jmx.AbstractKafkaJmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/17  下午6:23.
 */
public abstract class AbstractKafkaServerJmx extends AbstractKafkaJmx {

    protected boolean wentWrong = false;

    @Override
    protected void doFetchJmxMetric() {

        Map<String, BrokerStates> brokerStatesMap = fetchMBeanBrokerStates();
        Map<Integer, List<String>> groupId2KafkaCluster = loadKafkaClusters();
        for(List<String> cluster : groupId2KafkaCluster.values()){
            List<String> downBrokerIps = new ArrayList<String>();
            List<String> liveControllerIps = new ArrayList<String>();
            for(String kafkaIp : cluster){
                BrokerStates state = brokerStatesMap.get(kafkaIp);

                if (state == BrokerStates.RunningAsBroker) {
                    //nothing to do;
                } else if (state == BrokerStates.RunningAsController) {
                    liveControllerIps.add(kafkaIp);
                } else {
                    downBrokerIps.add(kafkaIp);
                }
            }
            checkKafkaStates(downBrokerIps, liveControllerIps, cluster);
        }

    }

    @Override
    protected int getInterval() {
        return 30;
    }

    @Override
    protected int getDelay() {
        return 10;
    }

    @Override
    protected String getJmxName(){
        return "BrokerState";
    }

    protected abstract void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster);

}
