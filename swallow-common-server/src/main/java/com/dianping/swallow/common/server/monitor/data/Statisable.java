package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.NavigableMap;


/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午5:04:56
 */
public interface Statisable<V> extends KeyMergeable {

    Long INFINITY = -1L;

    void add(Long time, V added);

    /**
     * 删除掉<time之前的数据
     *
     * @param time
     */
    void removeBefore(Long time);

    /**
     * >= startKey <= endKey
     *
     * @param qpx
     * @param startKey
     * @param endKey
     * @param intervalCount
     */
    void build(QPX qpx, Long startKey, Long endKey, int intervalCount);

    @JsonIgnore
    boolean isEmpty();

    @JsonIgnore
    NavigableMap<Long, StatisData> getData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey);

//    @JsonIgnore
//    NavigableMap<Long, StatisData> getDelayAndQps(StatisType type);
//
//    @JsonIgnore
//    NavigableMap<Long, StatisData> getDelayAndQps(StatisType type, Long startKey, Long stopKey);

    String toString(String key);

    public static class QpxData {

        private StatisData statisData;

        public QpxData(StatisData statisData) {
            this.statisData = statisData;
        }

        public Long getQpx(QPX qpx) {
            return statisData.getQpx(qpx);
        }

        public Long getTotal() {
            return statisData.getCount();
        }
    }

}
