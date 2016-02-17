package com.dianping.swallow.web.model.report;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Author   mingdongli
 * 16/1/21  下午7:48.
 */
public class ServerReport {

    @Id
    private String id;

    @Indexed(name = "IX_IP", direction = IndexDirection.DESCENDING, unique = true, dropDups = true)
    private String ip;

    @Indexed(name = "IX_TIMEKEY", direction = IndexDirection.DESCENDING, unique = false, dropDups = false)
    private long timeKey;

    private long count = 0L;

    private long throughput = 0L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(long timeKey) {
        this.timeKey = timeKey;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getThroughput() {
        return throughput;
    }

    public void setThroughput(long throughput) {
        this.throughput = throughput;
    }
}
