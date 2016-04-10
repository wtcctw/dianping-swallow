package com.dianping.swallow.common.server.monitor.data.structure;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitor;


/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月14日 下午3:29:56
 */
@Document(collection = "ProducerMonitorData")
public class ProducerMonitorData extends MonitorData {

    private static final long serialVersionUID = 1L;

    protected ProducerServerData all = new ProducerServerData();

    //for json deserialize
    public ProducerMonitorData() {
    }

    public ProducerMonitorData(String swallowServerIp) {
        super(swallowServerIp);
    }


    @Override
    protected void doMerge(Mergeable mergeData) {

        if (!(mergeData instanceof ProducerMonitorData)) {
            throw new IllegalArgumentException("wrong type " + mergeData.getClass());
        }

        ProducerMonitorData toMerge = (ProducerMonitorData) mergeData;
        all.merge(toMerge.all);

    }

    @Override
    protected Mergeable getTopic(KeyMergeable merge, String topic) {

        ProducerMonitorData pmd = (ProducerMonitorData) merge;
        return pmd.getTopic(topic);
    }

    @Override
    protected Mergeable getTopic(String topic) {

        return MapUtil.getOrCreate(all, topic, ProducerTopicData.class);
    }


    public void addData(final String topic, final String producerIp, final long messageId, final long msgSize, final long sendTime, final long saveTime) {

        String realTopic = topic;
        if (realTopic == null) {
            logger.error("[addData][topic null]");
            realTopic = "";
        }

        String realProducerIp = producerIp;
        if (realProducerIp == null) {
            logger.error("[addData][producerIp null]");
            realProducerIp = "";
        }
        ProducerTopicData ProducerData = MapUtil.getOrCreate(all, realTopic, ProducerTopicData.class);
        ProducerData.sendMessage(realProducerIp, messageId, msgSize, sendTime, saveTime);

    }


    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ProducerMonitorData)) {
            return false;
        }

        ProducerMonitorData cmp = (ProducerMonitorData) obj;

        return cmp.all.equals(all);
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + super.hashCode();
        hash = hash * 31 + all.hashCode();
        return hash;
    }

    @Override
    protected TotalMap<?> getTopicData(String topic) {
        return all.get(topic);
    }

    @Override
    protected void visitAllTopic(MonitorVisitor mv) {

        for (String topic : all.keySet()) {
            mv.visit(topic, all.get(topic));
        }
    }

    @Override
    public void buildTotal() {
        all.buildTotal();
    }

    @Override
    public Object getTotal() {
        return all.getTotal();
    }

    @Override
    public Set<String> getTopics() {
        return all.keySet();
    }

    @Override
    public TotalMap<?> getServerData() {

        return all;
    }

    @Override
    protected void doClone(MonitorData monitorData)
            throws CloneNotSupportedException {

        ProducerMonitorData producerMonitorData = (ProducerMonitorData) monitorData;
        producerMonitorData.all = (ProducerServerData) all.clone();
    }
}
