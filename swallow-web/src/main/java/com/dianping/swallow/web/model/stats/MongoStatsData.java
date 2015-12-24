package com.dianping.swallow.web.model.stats;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.model.event.StatisEvent;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author   mingdongli
 * 15/12/23  下午3:43.
 */
@Document(collection = "MONGO_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_IPS_TIMEKEY", def = "{ 'ips': 1, 'timeKey': 1}") })
public class MongoStatsData extends StatsData implements Mergeable{

    @Indexed(name = "IX_IPS", direction = IndexDirection.ASCENDING)
    private String ips;

    private Long count = 0L;

    //在生成StatisData时，已经补齐丢失的点，所以每个interval都是一样的
    private Long interval = 6L;

    public MongoStatsData(){

    }

    public MongoStatsData(String ips, long count, long interval, long time){
        this.ips = ips;
        this.count = count;
        this.interval = interval;
        this.setTimeKey(time);
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public Long getCount() {
        return count;
    }

    public long getInterval() {
        return interval;
    }

    public Long getQpx(QPX qpx) {
        if(interval <= 0){
            throw new RuntimeException("intervalCount should be positive");
        }
        if (qpx == QPX.MINUTE) {
            return this.count / (interval * AbstractCollector.SEND_INTERVAL) * 60;
        } else if (qpx == QPX.SECOND) {
            return this.count / (interval * AbstractCollector.SEND_INTERVAL);
        } else {
            throw new UnsupportedOperationException("unsupported QPX type");
        }
    }

    @Override
    public StatisEvent createEvent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(Mergeable merge) {

        if (!(merge instanceof MongoStatsData)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }

        MongoStatsData toMerge = (MongoStatsData) merge;
        this.count += toMerge.count;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        throw new UnsupportedOperationException("clone not support");
    }
}
