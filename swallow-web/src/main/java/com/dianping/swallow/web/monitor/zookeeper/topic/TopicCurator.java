package com.dianping.swallow.web.monitor.zookeeper.topic;

import com.dianping.swallow.web.monitor.jmx.listener.KafkaEventListener;
import com.dianping.swallow.web.monitor.zookeeper.CuratorAware;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/22  下午7:00.
 */
public interface TopicCurator extends CuratorAware, KafkaEventListener {

    List<String> getTopics(CuratorFramework curator) throws Exception;

    TopicDescription getTopicDescription(CuratorFramework curator, String topic);

    PartitionDescription getPartitionDescription(CuratorFramework curator, String topic, int partition);

}
