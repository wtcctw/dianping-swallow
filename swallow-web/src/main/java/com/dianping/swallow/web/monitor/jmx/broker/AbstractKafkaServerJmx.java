package com.dianping.swallow.web.monitor.jmx.broker;

import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.monitor.jmx.AbstractKafkaJmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/17  下午6:23.
 */
public abstract class AbstractKafkaServerJmx extends AbstractKafkaJmx {

    //以组区分
    protected Map<Integer, Boolean> id2States = new HashMap<Integer, Boolean>();

    @Override
    protected void doFetchJmxMetric() {

        Map<String, BrokerStates> brokerStatesMap = fetchMBeanBrokerStates();
        Map<Integer, List<String>> groupId2KafkaCluster = loadKafkaClusters();
        for(Map.Entry<Integer, List<String> > entry : groupId2KafkaCluster.entrySet()){
            List<String> cluster = entry.getValue();
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
            checkKafkaStates(downBrokerIps, liveControllerIps, cluster, entry.getKey());
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

    //BrokerState必须告警
    @Override
    public boolean isReport(Event event){
        return true;
    }

    @Override
    protected void initCustomConfig(){

        int nextGroupId = kafkaServerResourceService.getNextGroupId();
        for(int i = 0; i < nextGroupId; i++){
            id2States.put(i, Boolean.FALSE);
        }
    }

    protected abstract void checkKafkaStates(List<String> downBrokerIps, List<String> liveControllerIps, List<String> cluster, int id);

}
