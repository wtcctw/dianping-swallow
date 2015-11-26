package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisEvent;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * @author qiyin
 *         <p/>
 *         2015年7月31日 下午3:56:31
 */
@Document(collection = "CONSUMERID_STATS_DATA")
@CompoundIndexes({@CompoundIndex(name = "IX_TOPICNAME_CONSUMERID_TIMEKEY", def = "{'topicName': -1, 'consumerId': -1, 'timeKey': 1}")})
public class ConsumerIdStatsData extends ConsumerStatsData {

    private String topicName;

    private String consumerId;

    private long totalSendQps;

    private long totalSendDelay;

    private long totalAckQps;

    private long totalAckDelay;

    private long totalAccumulation;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public long getTotalSendQps() {
        return totalSendQps;
    }

    public void setTotalSendQps(long totalSendQps) {
        this.totalSendQps = totalSendQps;
    }

    public long getTotalSendDelay() {
        return totalSendDelay;
    }

    public void setTotalSendDelay(long totalSendDelay) {
        this.totalSendDelay = totalSendDelay;
    }

    public long getTotalAckQps() {
        return totalAckQps;
    }

    public void setTotalAckQps(long totalAckQps) {
        this.totalAckQps = totalAckQps;
    }

    public long getTotalAckDelay() {
        return totalAckDelay;
    }

    public void setTotalAckDelay(long totalAckDelay) {
        this.totalAckDelay = totalAckDelay;
    }

    public long getTotalAccumulation() {
        return totalAccumulation;
    }

    public void setTotalAccumulation(long totalAccumulation) {
        this.totalAccumulation = totalAccumulation;
    }

    public String generateKey() {
        return topicName + "&" + consumerId;
    }

    public boolean checkSendQpsFlu(long baseQps, long preQps, int flu) {
        return checkQpsFlu(getSendQps(), baseQps, preQps, flu, StatisType.SENDQPS_FLU);
    }

    public boolean checkSendDelay(long expectDelay) {
        return checkDelay(getSendDelay(), expectDelay, StatisType.SENDDELAY);
    }

    public boolean checkSendAccu(long expectAccu) {
        return checkAccu(getAccumulation(), expectAccu, StatisType.SENDACCU);
    }

    public boolean checkAckQpsFlu(long baseQps, long preQps, int flu) {
        return checkQpsFlu(getAckQps(), baseQps, preQps, flu, StatisType.ACKQPS_FLU);
    }

    public boolean checkAckDelay(long expectDelay) {
        return checkDelay(getAckDelay(), expectDelay, StatisType.ACKDELAY);
    }

    public void setTotalStatsDatas(ConsumerIdStatsData lastStatsData) {
        long currentSendQps = this.getSendQpsTotal();
        long currentAckQps = this.getAckQpsTotal();
        if (lastStatsData != null) {
            this.totalSendDelay = lastStatsData.getTotalSendDelay() + getSendDelay() * currentSendQps;
            this.totalSendQps = lastStatsData.getTotalSendQps() + currentSendQps;
            this.totalAckDelay = lastStatsData.getTotalAckDelay() + getAckDelay() * currentAckQps;
            this.totalAckQps = lastStatsData.getTotalAckQps() + currentAckQps;
            this.totalAccumulation = lastStatsData.getTotalAccumulation() + getAccumulation();
        } else {
            this.totalSendDelay = getSendDelay() * currentSendQps;
            this.totalSendQps = currentSendQps;
            this.totalAckDelay = getAckDelay() * currentAckQps;
            this.totalAckQps = currentAckQps;
            this.totalAccumulation = getAccumulation();
        }
    }

    @Override
    public String toString() {
        return "ConsumerIdStatsData [topicName=" + topicName + ", consumerId=" + consumerId + ", totalSendQps="
                + totalSendQps + ", totalSendDelay=" + totalSendDelay + ", totalAckQps=" + totalAckQps
                + ", totalAckDelay=" + totalAckDelay + ", totalAccumulation=" + totalAccumulation + "]"
                + super.toString();
    }

    @Override
    public StatisEvent createEvent() {
        return eventFactory.createConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName);
    }

}
