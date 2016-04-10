package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.AbstractTest;

import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/11/18  下午7:03.
 */
@SuppressWarnings("deprecation")
public class StatisDataTest extends AbstractTest {

    private static final Long startKey = 100L;

    private static final Long times = 10L;

    private static final Byte interval = 6;

    private static final long msgSize = 50L;

    private static NavigableMap<Long, StatisData> statisMap = new ConcurrentSkipListMap<Long, StatisData>();

    private static NavigableMap<Long, StatisData> mergeStatisMap = new ConcurrentSkipListMap<Long, StatisData>();

    @Before
    public void setUp() throws Exception {

        for (Long i = startKey; i <= startKey; ) {
            StatisData statisData1 = new StatisData(times * i, 2 * times * i, times * times * i, 2 * times * times * i, times * times * i * msgSize, 2 * times * times * i * msgSize, interval);
            StatisData statisData2 = new StatisData(times * i, 2 * times * i, times * times * i, 2 * times * times * i, times * times * i * msgSize, 2 * times * times * i * msgSize, interval);
            statisMap.put(i, statisData1);
            mergeStatisMap.put(i, statisData2);
            i += interval;
        }
    }

    @Test
    public void testMerge() {

        for (Map.Entry<Long, StatisData> entry : statisMap.entrySet()) {
            Long key = entry.getKey();
            entry.getValue().merge(mergeStatisMap.get(key));
            Assert.assertEquals(entry.getValue().getDelay().longValue(), mergeStatisMap.get(key).getDelay().longValue());
            Assert.assertEquals(entry.getValue().getTotalDelay().longValue(), 2 * mergeStatisMap.get(key).getTotalDelay().longValue());
            Assert.assertEquals(entry.getValue().getCount().longValue(), 2 * mergeStatisMap.get(key).getCount().longValue());
            Assert.assertEquals(entry.getValue().getTotalCount().longValue(), 2 * mergeStatisMap.get(key).getTotalCount().longValue());
        }


    }
}