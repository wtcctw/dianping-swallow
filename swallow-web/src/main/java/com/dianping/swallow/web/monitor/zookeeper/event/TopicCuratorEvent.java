package com.dianping.swallow.web.monitor.zookeeper.event;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/22  下午11:37.
 */
public class TopicCuratorEvent extends KafkaEvent {

    private int groupId;

    private String topic;

    private int partition;

    private List<Integer> replica;

    private List<Integer> isr;

    protected long checkInterval = 60 * 1000;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

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

    public List<Integer> getReplica() {
        return replica;
    }

    public void setReplica(List<Integer> replica) {
        this.replica = replica;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    public void setIsr(List<Integer> isr) {
        this.isr = isr;
    }

    @Override
    public void alarm() {
        switch (getServerType()) {
            case UNDERREPLICA_PARTITION_STATE:
                sendMessage(AlarmType.SERVER_UNDERREPLICA_PARTITION_STATE);
                break;
            case UNDERREPLICA_PARTITION_STATE_OK:
                sendMessage(AlarmType.SERVER_UNDERREPLICA_PARTITION_STATE_OK);
                break;
            default:
                break;
        }
    }

    @Override
    public String getMessage(String template) {
        String message = template;
        if (org.codehaus.plexus.util.StringUtils.isNotBlank(message)) {
            message = StringUtils.replace(message, AlarmMeta.TOPIC_TEMPLATE, getTopic());
            message = StringUtils.replace(message, AlarmMeta.CONSUMERID_TEMPLATE, String.valueOf(getPartition()));
            message = StringUtils.replace(message, AlarmMeta.CURRENTVALUE_TEMPLATE, prettyArray(getIsr()));
            message = StringUtils.replace(message, AlarmMeta.EXPECTEDVALUE_TEMPLATE, prettyArray(getReplica()));
        }
        return message;
    }

    private String prettyArray(List<Integer> list){
        StringBuilder stringBuilder = new StringBuilder();
        if(list == null){
            return org.apache.commons.lang.StringUtils.EMPTY;
        }
        stringBuilder.append("[");
        stringBuilder.append(org.apache.commons.lang.StringUtils.join(list, KafkaEvent.DELIMITOR));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public RelatedType getRelatedType() {
        return RelatedType.K_TOPIC;
    }

    @Override
    public boolean isSendAlarm(AlarmType alarmType, AlarmMeta alarmMeta) {
        String key = getIdentity() + KEY_SPLIT + getGroupId() + KEY_SPLIT + getTopic() + KEY_SPLIT + getPartition() + KEY_SPLIT + alarmType.getNumber();
        return isAlarm(lastAlarms, key, alarmMeta);
    }

    @Override
    public String getRelated() {
        return topic;
    }

    @Override
    protected List<String> displayIps() {
        throw new UnsupportedOperationException("not support");
    }
}
