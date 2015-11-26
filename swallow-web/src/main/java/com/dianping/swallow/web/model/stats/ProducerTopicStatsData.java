package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisEvent;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * @author qiyin
 *         <p/>
 *         2015年7月31日 下午3:57:04
 */
@Document(collection = "PRODUCER_TOPIC_STATS_DATA")
@CompoundIndexes({@CompoundIndex(name = "IX_TOPICNAME_TIMEKEY", def = "{'topicName': -1, 'timeKey': 1}")})
public class ProducerTopicStatsData extends ProducerStatsData {

    private String topicName;

    private long totalQps;

    private long totalDelay;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public long getTotalQps() {
        return totalQps;
    }

    public void setTotalQps(long totalQps) {
        this.totalQps = totalQps;
    }

    public long getTotalDelay() {
        return totalDelay;
    }

    public void setTotalDelay(long totalDelay) {
        this.totalDelay = totalDelay;
    }

    @Override
    public StatisEvent createEvent() {
        return eventFactory.createTopicEvent().setTopicName(topicName);
    }

    public boolean checkQpsFlu(long baseQps, long preQps, int flu) {
        return checkQpsFlu(this.getQps(), baseQps, preQps, flu, StatisType.SENDQPS_FLU);
    }

    public boolean checkDelay(long expectDelay) {
        return checkDelay(this.getDelay(), expectDelay, StatisType.SENDDELAY);
    }

    public void setTotalStatsDatas(ProducerTopicStatsData lastStatsData) {
        long currQps = this.getQpsTotal();
        if (lastStatsData != null) {
            this.totalQps = lastStatsData.getTotalQps() + currQps;
            this.totalDelay = lastStatsData.getTotalDelay() + this.getDelay() * currQps;
        } else {
            this.totalQps = currQps;
            this.totalDelay = this.getDelay() * currQps;
        }
    }

    @Override
    public String toString() {
        return "ProducerTopicStatsData [topicName=" + topicName + ", totalQps=" + totalQps + ", totalDelay="
                + totalDelay + "]" + super.toString();
    }

}
