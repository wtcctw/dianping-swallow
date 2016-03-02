package com.dianping.swallow.web.controller.kafka.topic;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.controller.kafka.AbstractDummyBaseZkPath;
import kafka.common.TopicExistsException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Author   mingdongli
 * 16/3/1  下午4:27.
 */
@Component
public class TopicBaseZkPath extends AbstractDummyBaseZkPath implements PartitionAssignment {

    private static final String TOPIC_PARTITION = "/brokers";

    private static final String IDS = "ids";

    @Override
    protected String baseZkPath() {
        return TOPIC_PARTITION;
    }

    @Override
    public List<Integer> getSortedBrokers(CuratorFramework curator) throws Exception {

        List<Integer> result = new ArrayList<Integer>();
        String brokerIdPath = zkPath(IDS);
        List<String> idStrings = curator.getChildren().forPath(brokerIdPath);
        if (idStrings != null) {
            Collections.sort(idStrings);
            for (String id : idStrings) {
                result.add(Integer.parseInt(id));
            }
        }
        return result;
    }

    @Override
    public Map<String, List<Integer>> assignReplicasToBrokers(List<Integer> brokerList, int nPartitions, int replicationFactor) throws Exception {
        return assignReplicasToBrokers(brokerList, nPartitions, replicationFactor, -1, -1);
    }

    @Override
    public Map<String, List<Integer>> assignReplicasToBrokers(List<Integer> brokerList, int nPartitions, int replicationFactor, int fixedStartIndex, int startPartitionId) throws Exception {

        if (nPartitions <= 0) {
            throw new Exception("number of partitions must be larger than 0");
        }
        if (replicationFactor <= 0) {
            throw new Exception("replication factor must be larger than 0");
        }
        if (replicationFactor > brokerList.size()) {
            throw new Exception("replication factor: " + replicationFactor +
                    " larger than available brokers: " + brokerList.size());
        }

        Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
        Random random = new Random();
        int startIndex = fixedStartIndex >= 0 ? fixedStartIndex : random.nextInt(brokerList.size());
        logger.debug("startIndex is " + startIndex);
        int currentPartitionId = startPartitionId >= 0 ? startPartitionId : 0;
        logger.debug("currentPartitionId is " + currentPartitionId);
        int nextReplicaShift = fixedStartIndex >= 0 ? fixedStartIndex : random.nextInt(brokerList.size());
        for (int i = 0; i < nPartitions; ++i) {
            if (currentPartitionId > 0 && (currentPartitionId % brokerList.size() == 0)) {
                nextReplicaShift += 1;
            }
            logger.debug("nextReplicaShift is " + nextReplicaShift);
            int firstReplicaIndex = (currentPartitionId + startIndex) % brokerList.size();
            List<Integer> replicaList = new ArrayList<Integer>();
            replicaList.add(brokerList.get(firstReplicaIndex));
            for (int j = 0; j < replicationFactor - 1; j++) {
                replicaList.add(brokerList.get(replicaIndex(firstReplicaIndex, nextReplicaShift, j, brokerList.size())));
            }
            result.put(Integer.toString(currentPartitionId), replicaList);
            currentPartitionId = currentPartitionId + 1;
        }

        checkAssignReplicas(result);
        return result;
    }

    private int replicaIndex(int firstReplicaIndex, int secondReplicaShift, int replicaIndex, int nBrokers) {
        int shift = 1 + (secondReplicaShift + replicaIndex) % (nBrokers - 1);
        return (firstReplicaIndex + shift) % nBrokers;
    }

    private void checkAssignReplicas(Map<String, List<Integer>> source) throws Exception{

        Set<Integer> sizeSet = new HashSet<Integer>();
        for(List<Integer> value : source.values()){
            sizeSet.add(value.size());
            Set<Integer> tmp = new HashSet<Integer>();
            tmp.addAll(value);
            if(value.size() != tmp.size()){
                throw new Exception("Duplicate replica assignment found");
            }
        }
        if(sizeSet.size() != 1){
            throw new Exception("All partitions should have the same number of replicas.");
        }
    }

    @Override
    public void writeTopicPartitionAssignment(CuratorFramework curator, String topic, Map<String, List<Integer>> replicaAssignment, boolean update) throws Exception {

        try {
            String topicPath = zkPath("topics/" + topic);
            JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
            Map<String, Object> partitionAssignment = new HashMap<String, Object>();
            partitionAssignment.put("version", 1);
            partitionAssignment.put("partitions", replicaAssignment);
            String replicaAssignmentJson = jsonBinder.toJson(partitionAssignment);

            if (!update) {
                logger.info("Topic creation " + replicaAssignmentJson);
                curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(topicPath, replicaAssignmentJson.getBytes());
            } else {
                logger.info("Topic update " + replicaAssignmentJson);
                updatePersistentPath(curator, topicPath, replicaAssignmentJson);
            }

            logger.debug("Updated path %s with %s for replica assignment".format(topicPath, replicaAssignmentJson));
        } catch (ZkNodeExistsException e1) {
            throw new TopicExistsException("topic %s already exists".format(topic));
        } catch (Throwable e2) {
            throw new Exception(e2.toString());
        }
    }

    @Override
    public String operationPath() {
        return zkPath("topics");
    }
}
