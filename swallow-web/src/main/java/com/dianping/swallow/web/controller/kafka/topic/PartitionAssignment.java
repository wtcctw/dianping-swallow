package com.dianping.swallow.web.controller.kafka.topic;

import com.dianping.swallow.web.controller.kafka.OperationPathAware;
import com.dianping.swallow.web.controller.kafka.broker.BrokerDescription;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:38.
 */
public interface PartitionAssignment extends BrokerDescription, OperationPathAware {

     Map<String, List<Integer>> assignReplicasToBrokers(List<Integer> brokerList, int nPartitions, int replicationFactor) throws Exception;

     Map<String, List<Integer>> assignReplicasToBrokers(List<Integer> brokerList, int nPartitions, int replicationFactor, int fixedStartIndex, int startPartitionId) throws Exception;

     void writeTopicPartitionAssignment(CuratorFramework curator, String topic, Map<String, List<Integer>> replicaAssignment, boolean update) throws Exception;

}
