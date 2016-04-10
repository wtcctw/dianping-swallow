package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.DataSpan;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.RetrieveType;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月20日 上午10:28:00
 */
public class MessageInfoStatisTest extends AbstractTest {

    private MessageInfoStatis messageInfoStatis;

    private final Long startKey = 100L, endKey = 160L;

    private final Long expectedQpx = 2L, expectedDelay = 10L;

    private final int intervalCount = 6;
    private final long msgSize = 50;

    @Before
    public void beforeMessageInfoCollectionTest() {

        messageInfoStatis = new MessageInfoStatis();

        prepareData();
    }

    private void prepareData() {

        MessageInfo info = new MessageInfo();

        for (Long key = startKey; key <= endKey; key++) {

            for (int i = 0; i < expectedQpx * AbstractCollector.SEND_INTERVAL; i++) {

                Long current = System.currentTimeMillis();
                info.addMessage(key, msgSize, current - expectedDelay, current);
            }
            try {
                messageInfoStatis.add(key, (MessageInfo) info.clone());
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Test
    public void testMergeDirty() throws CloneNotSupportedException {

        int addCount = 100;
        int wrong = 2;

        MessageInfo info1 = new MessageInfo();
        MessageInfo info2 = new MessageInfo();

        for (Long i = endKey + 1; i <= endKey + addCount; i++) {

            add(info1);
            add(info2);

            if (i <= endKey + wrong) {
                messageInfoStatis.add(i, (MessageInfo) info1.clone());
            } else {
                messageInfoStatis.add(i, (MessageInfo) info1.clone());
                messageInfoStatis.add(i, (MessageInfo) info2.clone());
            }
        }

        messageInfoStatis.build(QPX.SECOND, endKey + 1, endKey + addCount, 6);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, Long> delay = new ConcurrentSkipListMap<Long, Long>();
        NavigableMap<Long, QpxData> qpx = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            qpx.put(entry.getKey(), new QpxData(entry.getValue()));
            delay.put(entry.getKey(), entry.getValue().getDelay());
        }

        expectQpx(qpx.headMap(endKey + wrong, true), wrong / 6 + 1, expectedQpx * 2, 0L);
        expectQpx(qpx.tailMap(endKey + wrong + 1, true), (addCount - wrong - 1) / 6 - 1, expectedQpx * 2, expectedQpx * 2
                * AbstractCollector.SEND_INTERVAL * 6);

        expectDelay(delay.headMap(endKey + wrong, true), wrong / 6 + 1, 10L);
        expectDelay(delay.tailMap(endKey + wrong + 1, true), (addCount - wrong - 1) / 6 - 1, expectedDelay);

    }

    private void add(MessageInfo info) {

        for (int j = 0; j < AbstractCollector.SEND_INTERVAL * expectedQpx; j++) {
            Long current = System.currentTimeMillis();
            info.addMessage(1, msgSize, current - expectedDelay, current);
        }
    }

    @Test
    public void testAjustBigInterval() throws CloneNotSupportedException {

        int addCount = 20;
        addWrongData(addCount);

        messageInfoStatis.build(QPX.SECOND, endKey, endKey + addCount, intervalCount);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, Long> delay = new ConcurrentSkipListMap<Long, Long>();
        NavigableMap<Long, QpxData> qpx = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            qpx.put(entry.getKey(), new QpxData(entry.getValue()));
            delay.put(entry.getKey(), entry.getValue().getDelay());
        }

        expectDelay(delay, addCount / intervalCount, expectedDelay);
        expectQpx(qpx, addCount / intervalCount, expectedQpx, expectedQpx
                * intervalCount * AbstractCollector.SEND_INTERVAL);

    }

    @Test
    public void testAjustOneInterval() throws CloneNotSupportedException {

        int addCount = 20;
        addWrongData(addCount);

        messageInfoStatis.build(QPX.SECOND, endKey, endKey + addCount, 1);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, Long> delay = new ConcurrentSkipListMap<Long, Long>();
        NavigableMap<Long, QpxData> qpx = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            qpx.put(entry.getKey(), new QpxData(entry.getValue()));
            delay.put(entry.getKey(), entry.getValue().getDelay());
        }

        expectQpx(qpx.headMap(endKey + 1, true), 2, 0L, 0L);
        expectQpx(qpx.tailMap(endKey + 2, true), addCount - 2, 2L, expectedQpx * intervalCount
                * AbstractCollector.SEND_INTERVAL);

        expectDelay(delay.headMap(endKey + 1, true), 2, 0L);
        expectDelay(delay.tailMap(endKey + 2, true), addCount - 2, expectedDelay);

    }

    private void addWrongData(int addCount) throws CloneNotSupportedException {

        MessageInfo wrongInfo = new MessageInfo();

        for (int i = 0; i < addCount; i++) {

            add(wrongInfo);

            messageInfoStatis.add(endKey + i + 1, (MessageInfo) wrongInfo.clone());
        }
    }

    private void expectQpx(NavigableMap<Long, QpxData> data, int expectCount, Long expectedQpx, Long expectedTotal) {

        if (logger.isInfoEnabled()) {
            logger.info(data.toString());
        }

        Assert.assertEquals(expectCount, data.size());
        for (QpxData value : data.values()) {
            Assert.assertEquals(expectedQpx, value.getQpx(QPX.SECOND));
        }

    }

    private void expectDelay(NavigableMap<Long, Long> data, int expectCount, Long expectedData) {

        if (logger.isInfoEnabled()) {
            logger.info(data.toString());
        }

        Assert.assertEquals(expectCount, data.size());
        for (Long value : data.values()) {
            Assert.assertEquals(expectedData, value);
        }

    }

    @Test
    public void testQpx() {

        messageInfoStatis.build(QPX.SECOND, startKey, endKey, intervalCount);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, QpxData> qpx = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            qpx.put(entry.getKey(), new QpxData(entry.getValue()));
        }

        int size = (int) ((endKey - startKey) / intervalCount);
        Assert.assertEquals(size, qpx.size());

        for (QpxData qp : qpx.values()) {
            Assert.assertEquals(expectedQpx, qp.getQpx(QPX.SECOND));
        }
    }

    @Test
    public void testInsertLackData() {

        int insertCount = 100;

        messageInfoStatis.build(QPX.SECOND, startKey, endKey + insertCount, intervalCount);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, Long> delays = new ConcurrentSkipListMap<Long, Long>();
        NavigableMap<Long, QpxData> qpxs = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            qpxs.put(entry.getKey(), new QpxData(entry.getValue()));
            delays.put(entry.getKey(), entry.getValue().getDelay());
        }

        int size = (int) ((endKey - startKey + insertCount) / intervalCount);
        Assert.assertEquals(size, qpxs.size());
        Assert.assertEquals(size, delays.size());

        int firstSize = (int) ((endKey - startKey) / intervalCount);
        int count = 0;
        for (QpxData qpx : qpxs.values()) {

            if (count < firstSize) {
                Assert.assertEquals(expectedQpx, qpx.getQpx(QPX.SECOND));
                Assert.assertEquals(Long.valueOf(expectedQpx * AbstractCollector.SEND_INTERVAL * intervalCount),
                        qpx.getTotal());
            } else {
                Assert.assertEquals((Long) 0L, qpx.getQpx(QPX.SECOND));
            }
            count++;
        }

        count = 0;
        for (Long delay : delays.values()) {

            if (count < firstSize) {
                Assert.assertEquals(expectedDelay, delay);
            } else {
                Assert.assertEquals((Long) 0L, delay);
            }
            count++;
        }

    }

//    @Test
//    public void testGetMarginValue() {
//        MessageInfoStatis mnfoStatis = new MessageInfoStatis();
//        NavigableMap<Long, StatisData> data = getStatisData(DataSpan.LEFTMARGIN);
//        mnfoStatis.setStatisMap(data);
//        data = mnfoStatis.getData(RetrieveType.MIN_POINT, StatisType.SAVE, null, null);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(106, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = mnfoStatis.getData(RetrieveType.LESS_POINT, StatisType.SAVE, null, 101L);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(102L, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = mnfoStatis.getData(RetrieveType.LESS_POINT, StatisType.SAVE, null, 107L);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(107L, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = getStatisData(DataSpan.RIGHTMARGIN);
//        mnfoStatis.setStatisMap(data);
//        data = mnfoStatis.getDelayAndQps(StatisType.SEND, -1L, Long.MAX_VALUE);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(111, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = mnfoStatis.getDelayAndQps(StatisType.SEND, Long.MIN_VALUE, 106L);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(106L, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = mnfoStatis.getDelayAndQps(StatisType.SEND, Long.MIN_VALUE, 114L);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(113L, data.lastKey().longValue());
//        System.out.println(data.toString());
////----------------------------------------------------
//        data = mnfoStatis.getDelayAndQps(StatisType.SEND, 110L, Long.MAX_VALUE);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(111L, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//        data = mnfoStatis.getDelayAndQps(StatisType.SEND, Long.MIN_VALUE, 110L);
//        Assert.assertEquals(1, data.size());
//        Assert.assertEquals(109L, data.lastKey().longValue());
//        System.out.println(data.toString());
//
//
//    }
//
//    private NavigableMap<Long, StatisData> getStatisData(DataSpan dataSpan) {
//        Long i = 100L;
//        NavigableMap<Long, StatisData> data = new ConcurrentSkipListMap<Long, StatisData>();
//        if (DataSpan.LEFTMARGIN == dataSpan) {
//            for (; i < 106; ++i) {
//                if (i == 101L) {
//                    continue;
//                }
//                data.put(i, new StatisData());
//
//            }
//        }
//        i = 106L;
//        for (; i < 112; ++i) {
//            if (i == 110) {
//                continue;
//            }
//            data.put(i, new StatisData(10L + i, 20L + i, 100L + i, 200L + i,,, (byte) 6));
//        }
//        if (DataSpan.RIGHTMARGIN == dataSpan) {
//            for (; i < 118; ++i) {
//                if (i == 114) {
//                    continue;
//                }
//                data.put(i, new StatisData());
//            }
//        }
//        return data;
//
//    }

    @Test
    public void testDelay() {

        messageInfoStatis.build(QPX.SECOND, startKey, endKey, intervalCount);

        NavigableMap<Long, StatisData> statisData = messageInfoStatis.getData(RetrieveType.ALL_SECTION, StatisType.SAVE, null, null);
        NavigableMap<Long, Long> delays = new ConcurrentSkipListMap<Long, Long>();
        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
            delays.put(entry.getKey(), entry.getValue().getDelay());
        }

        int size = (int) ((endKey - startKey) / intervalCount);

        Assert.assertEquals(size, delays.size());

        for (Long delay : delays.values()) {

            Assert.assertEquals(expectedDelay, delay);
        }
    }

}
