package com.dianping.swallow.web.monitor.zookeeper.topic;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.dianping.swallow.web.monitor.zookeeper.AbstractCuratorAware;
import com.dianping.swallow.web.monitor.zookeeper.event.TopicCuratorEvent;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.yammer.metrics.core.MetricName;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/22  下午6:47.
 */
@Component
public class TopicCuratorImpl extends AbstractCuratorAware implements TopicCurator{

    private static final String TOPIC_DESCRIPTION = "/brokers/topics";

    private Map<TopicPartitionKey, Boolean> topicPartitionKeyMap = new HashMap<TopicPartitionKey, Boolean>();

    @Resource(name = "kafkaServerResourceService")
    protected KafkaServerResourceService kafkaServerResourceService;

    @Override
    protected void doFetchZkData() {
        Map<Integer, String> zkClusters = loadKafkaZkClusters();
        for(Map.Entry<Integer, String> entry : zkClusters.entrySet()){
            int groupId = entry.getKey();
            String zkServers = entry.getValue();
            CuratorFramework curator = getCurator(zkServers);
            try {
                List<String> topics = getTopics(curator);
                for(String topic : topics){
                    TopicDescription topicDescription = getTopicDescription(curator, topic);
                    Map<Integer, List<Integer>> part2Replica = topicDescription.getPartitions();
                    for(Map.Entry<Integer, List<Integer>> partreplica : part2Replica.entrySet()){
                        int partition = partreplica.getKey();
                        List<Integer> replica = partreplica.getValue();
                        PartitionDescription partitionDescription = getPartitionDescription(curator, topic, partition);
                        List<Integer> isr = partitionDescription.getIsr();

                        if(isr != null && replica != null && isr.size() < replica.size()){
                            reportTopicCuratorWrongEvent(groupId, topic, partition, isr, replica);
                        }else {
                            reportTopicCuratorOKEvent(groupId, topic, partition);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Get data from zk error");
            }

        }
    }

    @Override
    protected String baseZkPath() {
        return TOPIC_DESCRIPTION;
    }

    @Override
    public List<String> getTopics(CuratorFramework curator) throws Exception {
        return curator.getChildren().forPath(TOPIC_DESCRIPTION);
    }

    @Override
    public TopicDescription getTopicDescription(CuratorFramework curator, String topic) throws Exception {
        byte[] topicDescription = curator.getData().forPath(zkPath(topic));
        return jsonBinder.fromJson(new String(topicDescription), TopicDescription.class);
    }

    @Override
    public PartitionDescription getPartitionDescription(CuratorFramework curator, String topic, int partition) throws Exception {
        byte[] partitionDescription = curator.getData().forPath(topicPartitionStatePath(topic, partition));
        return jsonBinder.fromJson(new String(partitionDescription), PartitionDescription.class);
    }

    private String topicPartitionStatePath(String topic, int partition) {
        String path = new StringBuilder().append(topic).append(BACK_SLASH).append("partitions").append(BACK_SLASH)
                .append(partition).append(BACK_SLASH).append("state").toString();
        return zkPath(path);
    }

    @Override
    protected int getInterval() {
        return 60;
    }

    @Override
    protected int getDelay() {
        return 1;
    }

    @Override
    protected KafkaEvent createEvent() {
        return eventFactory.createTopicCuratorEvent();
    }

    private Map<Integer, String> loadKafkaZkClusters(){

        Map<Integer, String> groupId2KafkaZkCluster = new HashMap<Integer, String>();
        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for(KafkaServerResource kafkaServerResource : kafkaServerResources){
            int groupId = kafkaServerResource.getGroupId();
            String zkIps = groupId2KafkaZkCluster.get(groupId);
            if(zkIps == null){
                zkIps = kafkaServerResource.getZkServers();
                groupId2KafkaZkCluster.put(groupId, zkIps);
            }
        }
        return groupId2KafkaZkCluster;

    }

    private void reportTopicCuratorWrongEvent(int groupId, String topic, int partition, List<Integer> isr, List<Integer> replica){

        TopicPartitionKey topicPartitionKey = new TopicPartitionKey(groupId, topic, partition);
        topicPartitionKeyMap.put(topicPartitionKey, Boolean.TRUE);
        TopicCuratorEvent topicCuratorEvent = (TopicCuratorEvent) createEvent();
        topicCuratorEvent.setServerType(ServerType.UNDERREPLICA_PARTITION_STATE);
        topicCuratorEvent.setTopic(topic);
        topicCuratorEvent.setPartition(partition);
        topicCuratorEvent.setReplica(replica);
        topicCuratorEvent.setIsr(isr);
        report(topicCuratorEvent);
    }

    private void reportTopicCuratorOKEvent(int groupId, String topic, int partition) {

        TopicPartitionKey topicPartitionKey = new TopicPartitionKey(groupId, topic, partition);
        Boolean wentWrong = topicPartitionKeyMap.get(topicPartitionKey);
        if (wentWrong != null && wentWrong) { //恢复
            TopicCuratorEvent topicCuratorEvent = (TopicCuratorEvent) createEvent();
            topicCuratorEvent.setServerType(ServerType.UNDERREPLICA_PARTITION_STATE_OK);
            topicCuratorEvent.setTopic(topic);
            topicCuratorEvent.setPartition(partition);
            report(topicCuratorEvent);
        }
        topicPartitionKeyMap.put(topicPartitionKey, Boolean.FALSE);
    }

    @Override
    public Object getMBeanValue(String host, int port, MetricName metricName, Class<?> clazz) throws IOException {
        throw new UnsupportedOperationException("not support");
    }
}
