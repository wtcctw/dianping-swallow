package com.dianping.swallow.common.internal.monitor.impl;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/11/5  下午6:50.
 */
public class MapMergeableImplTest {

    MapMergeableImpl<Long, StatisData> mapStatisDataMergeableImp = new MapMergeableImpl<Long, StatisData>();

    MapMergeableImpl<Long, QpxData> mapQpxDataMergeableImp = new MapMergeableImpl<Long, QpxData>();

    NavigableMap<Long, StatisData> mapStatisDataToMerge = new ConcurrentSkipListMap<Long, StatisData>();

    NavigableMap<Long, QpxData> mapQpxDataToMerge = new ConcurrentSkipListMap<Long, QpxData>();

    @Before
    public void setUp() throws Exception {

        for (long i = 0; i < 20; i++) {
            StatisData statisData = new StatisData();
            statisData.setCount(i).setDelay(i).setTotalCount(i + 1).setTotalDelay(i + 1);
            mapStatisDataToMerge.put(i, statisData);
            mapQpxDataToMerge.put(i, new QpxData(i, i + 1));
        }
    }


    @Test
    public void testEmpty() {
        System.out.println("testEmpty");
        mapStatisDataMergeableImp.merge(mapStatisDataToMerge);
        NavigableMap<Long, StatisData> result1 = mapStatisDataMergeableImp.getToMerge();
        for (Map.Entry<Long, StatisData> entry : result1.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
        }
        System.out.println();
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for (Map.Entry<Long, QpxData> entry : result2.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());

        }
        Assert.assertEquals(mapStatisDataMergeableImp.getToMerge().toString(), mapStatisDataToMerge.toString());
    }

    @Test
    public void testNotModifyOriginalData() {
        System.out.println("testEmpty");
        mapStatisDataMergeableImp.merge(mapStatisDataToMerge);
        mapStatisDataMergeableImp.merge(mapStatisDataToMerge);
        System.out.println("mapLongToMerge is not change");
        NavigableMap<Long, StatisData> result1 = mapStatisDataMergeableImp.getToMerge();
        for (Map.Entry<Long, StatisData> entry : result1.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
        }
        Assert.assertNotEquals(mapStatisDataMergeableImp.getToMerge().toString(), mapStatisDataToMerge.toString());

        System.out.println();
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        System.out.println("mapQpxDataToMerge is not changed");
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for (Map.Entry<Long, QpxData> entry : result2.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        for (Map.Entry<Long, QpxData> entry : result2.entrySet()) {
            Long key = entry.getKey();
            QpxData value1 = entry.getValue();
            QpxData value2 = mapQpxDataToMerge.get(key);
            Assert.assertEquals(value1.getQpx().longValue(), value2.getQpx().longValue() * 2);
            Assert.assertEquals(value1.getTotal().longValue(), value2.getTotal().longValue() * 2);
        }
    }

    @Test
    public void testNotEmpty() {
        System.out.println("testNotEmpty");
        mapStatisDataMergeableImp.setToMerge(mapStatisDataToMerge);
        mapStatisDataMergeableImp.merge(mapStatisDataToMerge);
        NavigableMap<Long, StatisData> result1 = mapStatisDataMergeableImp.getToMerge();
        for (Map.Entry<Long, StatisData> entry : result1.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
        }
        System.out.println();
        mapQpxDataMergeableImp.setToMerge(mapQpxDataToMerge);
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for (Map.Entry<Long, QpxData> entry : result2.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    @Test
    public void testGetToMerge() {

        MapMergeableImpl<Long, StatisData> result = new MapMergeableImpl<Long, StatisData>();
        NavigableMap<Long, StatisData> aaa = new ConcurrentSkipListMap<Long, StatisData>(mapStatisDataToMerge);
        NavigableMap<Long, StatisData> bbb = new ConcurrentSkipListMap<Long, StatisData>(mapStatisDataToMerge);
        NavigableMap<Long, StatisData> ccc = new ConcurrentSkipListMap<Long, StatisData>(mapStatisDataToMerge);
        //aaa,bbb,ccc will refer to same value
        result.setToMerge(aaa);
        result.merge(mapStatisDataToMerge);
        aaa = result.getToMerge();

        result.setToMerge(bbb);
        result.merge(mapStatisDataToMerge);
        result.merge(mapStatisDataToMerge);
        bbb = result.getToMerge();
        Assert.assertEquals(aaa, bbb);

        result.setToMerge(ccc);
        result.merge(mapStatisDataToMerge);
        result.merge(mapStatisDataToMerge);
        result.merge(mapStatisDataToMerge);
        ccc = result.getToMerge();
        Assert.assertEquals(ccc, bbb);

        Map<Long, Long> map = new HashMap<Long, Long>();
        map.put(0L, 0L);
        map.put(1L, 0L);
        map.put(2L, 0L);
        map.put(3L, 0L);
        Collection<Long> set = map.values();
        Set<Long> newSet = new HashSet<Long>(set);
        for (Long v : newSet) {
            System.out.println(v);
        }
        Assert.assertEquals(newSet.size(), 1);

    }

    @Test
    public void testSetAndMerge(){

        MapMergeableImpl<Long, StatisData> result1 = new MapMergeableImpl<Long, StatisData>();
        MapMergeableImpl<Long, StatisData> result2 = new MapMergeableImpl<Long, StatisData>();
        result1.setToMerge(mapStatisDataToMerge);
        result2.merge(mapStatisDataToMerge);
        Assert.assertEquals(result1.getToMerge().toString(), result2.getToMerge().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeSubMap(){

        MapMergeableImpl<Long, StatisData> result = new MapMergeableImpl<Long, StatisData>();
        result.setToMerge(mapStatisDataToMerge.subMap(3L, true, 6L, true));
        result.merge(mapStatisDataToMerge.subMap(2L, true, 5L, true));
    }

    public static class QpxData implements Mergeable {

        private Long qpx;

        private Long total;

        public QpxData() {

        }

        public QpxData(Long qpx, Long total) {
            this.qpx = qpx;
            this.total = total;
        }

        public Long getQpx() {
            return qpx;
        }

        public void setQpx(Long qpx) {
            this.qpx = qpx;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        @Override
        public void merge(Mergeable merge) {
            if (!(merge instanceof QpxData)) {
                throw new IllegalArgumentException("wrong type " + merge.getClass());
            }

            QpxData toMerge = (QpxData) merge;
            if (total == null) {
                total = new Long(0);
            }
            total += toMerge.getTotal();
            if (qpx == null) {
                qpx = new Long(0);
            }
            qpx += toMerge.getQpx();
        }

        @Override
        public Object clone() throws CloneNotSupportedException {

            QpxData info = (QpxData) super.clone();
            info.setQpx(this.qpx);
            info.setTotal(this.total);
            return info;
        }
    }

    public static class StatisData implements Mergeable {

        private Long delay;

        private Long totalDelay;

        private Long count;

        private Long totalCount;

        public Long getDelay() {
            return delay;
        }

        public StatisData setDelay(Long delay) {
            this.delay = delay;
            return this;
        }

        public Long getTotalDelay() {
            return totalDelay;
        }

        public StatisData setTotalDelay(Long totalDelay) {
            this.totalDelay = totalDelay;
            return this;
        }

        public Long getCount() {
            return count;
        }

        public StatisData setCount(Long count) {
            this.count = count;
            return this;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public StatisData setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

//        public Long getQpx(QPX qpx) {
//            if (qpx == QPX.MINUTE) {
//                return this.count * 2;
//            } else if (qpx == QPX.SECOND) {
//                return this.count / 30;
//            } else {
//                throw new UnsupportedOperationException("unsupported QPX type");
//            }
//        }

        @Override
        public void merge(Mergeable merge) {
            if (!(merge instanceof StatisData)) {
                throw new IllegalArgumentException("wrong type " + merge.getClass());
            }

            StatisData toMerge = (StatisData) merge;
            if (this.delay == null) {
                this.delay = 0L;
            }
            if (this.count == null) {
                this.count = 0L;
            }
            if (this.totalCount == null) {
                this.totalCount = 0L;
            }
            if (this.totalDelay == null) {
                this.totalDelay = 0L;
            }
            if (this.count + toMerge.count <= 0) {
                this.delay = 0L;
            } else {
                this.delay = (this.delay * this.count + toMerge.delay * toMerge.count) / (this.count + toMerge.count);
            }
            this.count += toMerge.count;
            this.totalCount += toMerge.totalCount;
            this.totalDelay += toMerge.totalDelay;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            throw new UnsupportedOperationException("clone not support");
        }

        @Override
        public String toString() {
            return "StatisData{" +
                    "delay=" + delay +
                    ", totalDelay=" + totalDelay +
                    ", count=" + count +
                    ", totalCount=" + totalCount +
                    '}';
        }
    }
}