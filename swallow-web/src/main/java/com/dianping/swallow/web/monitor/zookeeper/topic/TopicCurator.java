package com.dianping.swallow.web.monitor.zookeeper.topic;

import com.dianping.swallow.web.monitor.zookeeper.CuratorAware;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/22  下午7:00.
 */
public interface TopicCurator extends CuratorAware {

    List<String> getTopics(CuratorFramework curator) throws Exception;

    TopicDescription getTopicDescription(CuratorFramework curator, String topic) throws Exception;

    PartitionDescription getPartitionDescription(CuratorFramework curator, String topic, int partition) throws Exception;

}
