package com.dianping.swallow.web.controller.kafka.topic;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author   mingdongli
 * 16/3/1  下午10:42.
 */
public class TopicBaseZkPathTest extends AbstractZkPath{

    private TopicBaseZkPath topicBaseZkPath = new TopicBaseZkPath();

    @Test
    public void testGetSortedBrokers() throws Exception {

        List<Integer> brokers = topicBaseZkPath.getSortedBrokers(curator);
        for (int i = 0; i < brokers.size(); ++i) {
            Assert.assertEquals(brokers.get(i).intValue(), i);
        }
    }

    @Test
    public void testAssignReplicasToBrokers() throws Exception {

        List<Integer> brokers = topicBaseZkPath.getSortedBrokers(curator);
        Map<String, List<Integer>> result =  topicBaseZkPath.assignReplicasToBrokers(brokers, N_PARTITION, N_REPLICATION);
        System.out.println(result.toString());
        Set<Integer> sizeSet = new HashSet<Integer>();
        for(List<Integer> value : result.values()){
            sizeSet.add(value.size());
            Set<Integer> tmp = new HashSet<Integer>();
            tmp.addAll(value);
            Assert.assertEquals(value.size(), tmp.size());
        }
        Assert.assertEquals(sizeSet.size(), 1);
    }

    @Test
    public void testWriteTopicPartitionAssignment() throws Exception {

        List<Integer> brokers = topicBaseZkPath.getSortedBrokers(curator);
        Map<String, List<Integer>> result =  topicBaseZkPath.assignReplicasToBrokers(brokers, N_PARTITION, N_REPLICATION);
        topicBaseZkPath.writeTopicPartitionAssignment(curator, TOPIC, result, false);
        String topicPath = topicBaseZkPath.zkPath("topics/" + TOPIC);
        doDelZnodeRecursively(topicPath);
    }
}