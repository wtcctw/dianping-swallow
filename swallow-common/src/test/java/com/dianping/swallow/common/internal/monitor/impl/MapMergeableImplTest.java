package com.dianping.swallow.common.internal.monitor.impl;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/11/5  下午6:50.
 */
public class MapMergeableImplTest{

    MapMergeableImpl<Long, Long> mapLongMergeableImp = new MapMergeableImpl<Long, Long>();

    MapMergeableImpl<Long, QpxData> mapQpxDataMergeableImp = new MapMergeableImpl<Long, QpxData>();

    NavigableMap<Long, Long> mapLongToMerge = new ConcurrentSkipListMap<Long, Long>();

    NavigableMap<Long, QpxData> mapQpxDataToMerge = new ConcurrentSkipListMap<Long, QpxData>();

    @Before
    public void setUp() throws Exception {

        for(long i = 0; i < 20; i++){
            mapLongToMerge.put(i, i);
            mapQpxDataToMerge.put(i, new QpxData(i, i + 1));
        }
    }


    @Test
    public void testEmpty(){
        System.out.println("testEmpty");
        mapLongMergeableImp.merge(mapLongToMerge);
        NavigableMap<Long, Long> result1 = mapLongMergeableImp.getToMerge();
        for(Map.Entry<Long, Long> entry : result1.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for(Map.Entry<Long, QpxData> entry : result2.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());

        }
        Assert.assertEquals(mapLongMergeableImp.getToMerge(), mapLongToMerge);
    }

    @Test
    public void testNotModifyOriginalData(){
        System.out.println("testEmpty");
        mapLongMergeableImp.merge(mapLongToMerge);
        mapLongMergeableImp.merge(mapLongToMerge);
        System.out.println("mapLongToMerge is not change");
        NavigableMap<Long, Long> result1 = mapLongMergeableImp.getToMerge();
        for(Map.Entry<Long, Long> entry : result1.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        Assert.assertNotEquals(mapLongMergeableImp.getToMerge(), mapLongToMerge);

        System.out.println();
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        System.out.println("mapQpxDataToMerge is not changed");
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for(Map.Entry<Long, QpxData> entry : result2.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        for(Map.Entry<Long, QpxData> entry : result2.entrySet()){
            Long key = entry.getKey();
            QpxData value1 = entry.getValue();
            QpxData value2 = mapQpxDataToMerge.get(key);
            Assert.assertEquals(value1.getQpx().longValue(), value2.getQpx().longValue() * 2);
            Assert.assertEquals(value1.getTotal().longValue(), value2.getTotal().longValue() * 2);
        }
    }

    @Test
    public void testNotEmpty(){
        System.out.println("testNotEmpty");
        mapLongMergeableImp.setToMerge(mapLongToMerge);
        mapLongMergeableImp.merge(mapLongToMerge);
        NavigableMap<Long, Long> result1 = mapLongMergeableImp.getToMerge();
        for(Map.Entry<Long, Long> entry : result1.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
        mapQpxDataMergeableImp.setToMerge(mapQpxDataToMerge);
        mapQpxDataMergeableImp.merge(mapQpxDataToMerge);
        NavigableMap<Long, QpxData> result2 = mapQpxDataMergeableImp.getToMerge();
        for(Map.Entry<Long, QpxData> entry : result2.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException(){
        System.out.println("testException");
        MapMergeableImpl<Long, Integer> mapIntegerMergeableImp = new MapMergeableImpl<Long, Integer>();
        NavigableMap<Long, Integer> mapIntegerToMerge = new ConcurrentSkipListMap<Long, Integer>();

        for(long i = 0; i < 20; i++){
            mapIntegerToMerge.put(i, (int) i);
        }
        mapIntegerMergeableImp.merge(mapIntegerToMerge);
    }

    public static class QpxData implements Mergeable{

        private Long qpx;

        private Long total;

        public QpxData(){

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
            if(!(merge instanceof QpxData)){
                throw new IllegalArgumentException("wrong type " + merge.getClass());
            }

            QpxData toMerge = (QpxData) merge;
            if(total == null){
                total = new Long(0);
            }
            total += toMerge.getTotal();
            if(qpx == null){
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
}