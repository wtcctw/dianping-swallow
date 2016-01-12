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
 *         2015年9月6日 上午9:46:03
 */
@Document(collection = "CONSUMER_IP_STATS_DATA")
@CompoundIndexes({@CompoundIndex(name = "IX_TOPICNAME_CONSUMERID_IP_TIMEKEY", def = "{'topicName':-1, 'consumerId': -1, 'ip': -1, 'timeKey': 1}")})
public class ConsumerIpStatsData extends AbstractIpStatsData {

    private String consumerId;

    public ConsumerIpStatsData() {
        eventType = EventType.CONSUMER;
    }

    private long sendQps;

    @Transient
    private long sendQpsTotal;

    private long sendDelay;

    @Transient
    private long ackQpsTotal;

    private long ackQps;

    private long ackDelay;

    private long accumulation;

    public long getSendQps() {
        return sendQps;
    }

    public void setSendQps(long sendQps) {
        this.sendQps = sendQps;
    }

    public long getSendDelay() {
        return sendDelay;
    }

    public void setSendDelay(long sendDelay) {
        this.sendDelay = sendDelay;
    }

    public long getAckQps() {
        return ackQps;
    }

    public void setAckQps(long ackQpx) {
        this.ackQps = ackQpx;
    }

    public long getAckDelay() {
        return ackDelay;
    }

    public void setAckDelay(long ackDelay) {
        this.ackDelay = ackDelay;
    }

    public long getAccumulation() {
        return accumulation;
    }

    public void setAccumulation(long accumulation) {
        this.accumulation = accumulation;
    }

    @Override
    public String toString() {
        return "ConsumerIpStatsData [consumerId=" + consumerId + ", sendQps=" + sendQps + ", sendQpsTotal="
                + sendQpsTotal + ", sendDelay=" + sendDelay + ", ackQpsTotal=" + ackQpsTotal + ", ackQps=" + ackQps
                + ", ackDelay=" + ackDelay + ", accumulation=" + accumulation + "]";
    }

    @JsonIgnore
    public long getSendQpsTotal() {
        return sendQpsTotal;
    }

    @JsonIgnore
    public void setSendQpsTotal(long sendQpsTotal) {
        this.sendQpsTotal = sendQpsTotal;
    }

    @JsonIgnore
    public long getAckQpsTotal() {
        return ackQpsTotal;
    }

    @JsonIgnore
    public void setAckQpsTotal(long ackQpsTotal) {
        this.ackQpsTotal = ackQpsTotal;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public boolean hasStatsData() {
        return hasStatsData(0L, 0L);
    }

    public boolean hasStatsData(long qpsThreshold) {
        if (this.getSendQps() >= qpsThreshold || this.getAckQps() >= qpsThreshold) {
            return true;
        }
        return false;
    }

    public boolean hasStatsData(long qpsThreshold, long totalThreshold) {
        if (this.getSendQps() <= qpsThreshold && this.getSendQpsTotal() <= totalThreshold && this.getAckQps() <= qpsThreshold
                && this.getAckQpsTotal() <= totalThreshold) {
            return false;
        }
        return true;
    }

    @JsonIgnore
    @Override
    public long getQpsCount() {
        return sendQpsTotal;
    }

    @Override
    public IpStatsDataKey createStatsDataKey() {
        return new ConsumerIpStatsDataKey(this);
    }

    public static class ConsumerIpStatsDataKey extends IpStatsDataKey {

        public ConsumerIpStatsDataKey(ConsumerIpStatsData ipStatsData) {
            super(ipStatsData);
            this.consumerId = ipStatsData.getConsumerId();
        }

        private String consumerId;

        public String getConsumerId() {
            return consumerId;
        }

        @Override
        public String toString() {
            return "ConsumerIpStatsDataKey [consumerId=" + consumerId + super.toString() + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((getTopicName() == null) ? 0 : getTopicName().hashCode());
            result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
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
            ConsumerIpStatsDataKey other = (ConsumerIpStatsDataKey) obj;
            if (getTopicName() == null) {
                if (other.getTopicName() != null)
                    return false;
            } else if (!getTopicName().equals(other.getTopicName()))
                return false;
            if (consumerId == null) {
                if (other.consumerId != null)
                    return false;
            } else if (!consumerId.equals(other.consumerId))
                return false;
            if (getIp() == null) {
                if (other.getIp() != null)
                    return false;
            } else if (!getIp().equals(other.getIp()))
                return false;
            return true;
        }
    }
}
