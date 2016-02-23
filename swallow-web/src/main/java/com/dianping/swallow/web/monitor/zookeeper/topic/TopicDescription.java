package com.dianping.swallow.web.monitor.zookeeper.topic;

import java.util.List;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/2/22  下午7:03.
 */
public class TopicDescription {

    private int version;

    private Map<Integer, List<Integer>> partitions;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Integer, List<Integer>> getPartitions() {
        return partitions;
    }

    public void setPartitions(Map<Integer, List<Integer>> partitions) {
        this.partitions = partitions;
    }
}
