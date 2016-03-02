package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.controller.kafka.config.TopicConfig;
import com.dianping.swallow.web.controller.kafka.topic.PartitionAssignment;
import com.dianping.swallow.web.monitor.zookeeper.AbstractBaseZkPath;
import com.dianping.swallow.web.monitor.zookeeper.CuratorAware;
import com.dianping.swallow.web.monitor.zookeeper.CuratorConfig;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.KafkaService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public boolean createTopic(String zkServers, String topic, int partitions, int replicationFactor) {
        return createTopic(zkServers, topic, partitions, replicationFactor, new HashMap<String, Object>());
    }

    @Override
    public boolean createTopic(String zkServers, String topic, int partitions, int replicationFactor, Map<String, Object> config) {

        CuratorFramework curator = null;
        try {
            curator = getCurator(zkServers);
            List<Integer> sortedBrokers = partitionAssignment.getSortedBrokers(curator);
            Map<String, List<Integer>> part2Replicas = partitionAssignment.assignReplicasToBrokers(sortedBrokers, partitions, replicationFactor);
            topicConfig.writeTopicConfig(curator, topic, config);
            partitionAssignment.writeTopicPartitionAssignment(curator, topic, part2Replicas, false);
            return true;
        }catch (Exception e){
            logger.error(String.format("create topic %s error", topic));
            try {
                cleanUp(curator, topic);
            } catch (Exception e1) {
                logger.error(e1.getCause());
            }
            return false;
        }finally {
            if(curator != null){
                curator.close();
            }
        }

    }

    @Override
    public boolean cleanUpAfterCreateFail(String zkServers, String topic){

        CuratorFramework curator = null;
        try {
            curator = getCurator(zkServers);
            cleanUp(curator, topic);
            return true;
        }catch (Exception e){
            logger.error(String.format("clean up topic %s error", topic), e);
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
        if(StringUtils.isBlank(parent) || StringUtils.isBlank(child)){
            throw new UnsupportedOperationException("parent and child must be nonempty");
        }
        return new StringBuilder().append(parent).append(AbstractBaseZkPath.BACK_SLASH).append(child).toString();
    }

    private void delZnodeRecursively(CuratorFramework curator, String path) {
        try {
            List<String> childList = curator.getChildren().forPath(path);
            if (CollectionUtils.isEmpty(childList)) {
                curator.delete().forPath(path);
            } else {
                for(String childName: childList) {
                    String childPath = path + "/" + childName;
                    List<String> grandChildList = curator.getChildren().forPath(childPath);
                    if (CollectionUtils.isEmpty(grandChildList)) {
                        curator.delete().forPath(childPath);
                    } else {
                        delZnodeRecursively(curator, childPath);
                    }
                }
                curator.delete().forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete node recursively", e);
        }
    }

    private void cleanUp(CuratorFramework curator, String topic) throws Exception{
        String topicPath = zkPathFrom(topicConfig.operationPath(), topic);
        delZnodeRecursively(curator, topicPath);
        String topicConfigPath = zkPathFrom(partitionAssignment.operationPath(), topic);
        delZnodeRecursively(curator, topicConfigPath);
    }

    public void setTopicConfig(TopicConfig topicConfig) {
        this.topicConfig = topicConfig;
    }

    public void setPartitionAssignment(PartitionAssignment partitionAssignment) {
        this.partitionAssignment = partitionAssignment;
    }
}
