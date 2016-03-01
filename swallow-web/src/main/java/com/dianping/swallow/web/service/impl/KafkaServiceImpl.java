package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.controller.kafka.config.TopicConfig;
import com.dianping.swallow.web.controller.kafka.topic.PartitionAssignment;
import com.dianping.swallow.web.monitor.zookeeper.CuratorAware;
import com.dianping.swallow.web.monitor.zookeeper.CuratorConfig;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.KafkaService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:48.
 */
@Service("kafkaService")
public class KafkaServiceImpl extends AbstractSwallowService implements KafkaService, CuratorAware {

    @Autowired
    private TopicConfig topicConfig;

    @Autowired
    private PartitionAssignment partitionAssignment;

    @Override
    public boolean createTopic(String zkServers, String topic, int partitions, int replicationFactor, Map<String, Object> config) {

        CuratorFramework curator = null;
        try {
            curator = getCurator(zkServers);
            List<Integer> sortedBrokers = partitionAssignment.getSortedBrokers(curator);
            Map<String, List<Integer>> part2Replicas = partitionAssignment.assignReplicasToBrokers(sortedBrokers, partitions, replicationFactor, -1, -1);
            topicConfig.writeTopicConfig(curator, topic, config);
            partitionAssignment.writeTopicPartitionAssignment(curator, topic, part2Replicas, false);
            return true;
        }catch (Exception e){
            logger.error(String.format("create topic %s error", topic));
            return false;
        }finally {
            if(curator != null){
                curator.close();
            }
        }

    }

    @Override
    public CuratorFramework getCurator(CuratorConfig config) {
        CuratorFramework curator = CuratorFrameworkFactory.newClient(config.getZkConnect(),
                new BoundedExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxSleepTimeMs(), config.getZkMaxRetry()));
        curator.start();
        return curator;
    }

    @Override
    public CuratorFramework getCurator(String zkConnect) {
        CuratorConfig curatorConfig = new CuratorConfig(zkConnect);
        return getCurator(curatorConfig);
    }

    @Override
    public String zkPath(String path) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    public String zkPathFrom(String parent, String child) {
        throw new UnsupportedOperationException("not support");
    }
}
