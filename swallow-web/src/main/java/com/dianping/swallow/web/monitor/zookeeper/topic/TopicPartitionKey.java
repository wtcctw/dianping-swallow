package com.dianping.swallow.web.monitor.zookeeper.topic;

/**
 * Author   mingdongli
 * 16/2/22  下午10:24.
 */
public class TopicPartitionKey {

    public TopicPartitionKey(int groupId, String topic, int partition){
        this.groupId = groupId;
        this.topic = topic;
        this.partition = partition;
    }

    private String topic;

    private int partition;

    private int groupId;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicPartitionKey that = (TopicPartitionKey) o;

        if (partition != that.partition) return false;
        if (groupId != that.groupId) return false;
        return !(topic != null ? !topic.equals(that.topic) : that.topic != null);

    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + partition;
        result = 31 * result + groupId;
        return result;
    }
}
