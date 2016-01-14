package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.EventType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author qiyin
 *         <p/>
 *         2015年9月6日 上午9:45:54
 */
@Document(collection = "PRODUCER_IP_STATS_DATA")
@CompoundIndexes({@CompoundIndex(name = "IX_TOPICNAME_IP_TIMEKEY", def = "{'topicName': -1, 'ip': -1, 'timeKey': 1}")})
public class ProducerIpStatsData extends AbstractIpStatsData {

    public ProducerIpStatsData() {
        eventType = EventType.PRODUCER;
    }

    private long qps;

    @Transient
    private long qpsTotal;

    private long delay;

    public long getQps() {
        return qps;
    }

    public void setQps(long qps) {
        this.qps = qps;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "ProducerIpStatsData [qps=" + qps + ", qpsTotal=" + qpsTotal + ", delay=" + delay + "]";
    }

    @JsonIgnore
    public long getQpsTotal() {
        return qpsTotal;
    }

    @JsonIgnore
    public void setQpsTotal(long qpsTotal) {
        this.qpsTotal = qpsTotal;
    }

    public boolean hasStatsData() {
        if (getQps() > 0 || getQpsTotal() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasStatsData(long qpsThreshold) {
        if (this.getQps() < qpsThreshold) {
            return false;
        }
        return true;
    }

    @JsonIgnore
    @Override
    public long getMessageCount() {
        return qpsTotal;
    }

    @Override
    public IpStatsDataKey createStatsDataKey() {
        return new ProducerIpStatsDataKey(this);
    }

    public static class ProducerIpStatsDataKey extends IpStatsDataKey {
        public ProducerIpStatsDataKey(ProducerIpStatsData ipStatsData) {
            super(ipStatsData);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((getTopicName() == null) ? 0 : getTopicName().hashCode());
            result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ProducerIpStatsDataKey other = (ProducerIpStatsDataKey) obj;
            if (getTopicName() == null) {
                if (other.getTopicName() != null)
                    return false;
            } else if (!getTopicName().equals(other.getTopicName()))
                return false;
            if (getIp() == null) {
                if (other.getIp() != null)
                    return false;
            } else if (!getIp().equals(other.getIp()))
                return false;

            return true;
        }

        @Override
        public String toString() {
            return "ProducerIpStatsDataKey [" + super.toString() + "]";
        }

    }

}