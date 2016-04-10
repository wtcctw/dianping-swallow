package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月21日 上午10:40:57
 */
public class ProducerServerDataTest extends AbstractServerDataTest {

    private ProducerAllData producerAllData;

    @Before
    public void beforeProducerServerDataTest() {

        producerAllData = new ProducerAllData();
        prepareData(producerAllData);
        producerAllData.build(QPX.SECOND, startKey, endKey, intervalCount);
    }

    @Test
    public void testRetriever() {

        String server = ips[0];
        String topic = topics[0];
        String ip = ips[0];

        System.out.println(producerAllData.getKeys(new CasKeys(server, topic)));
        System.out.println(producerAllData.getKeys(new CasKeys("total", topic)));
        NavigableMap<Long, StatisData> res1 = producerAllData.getStatisData(new CasKeys("total", topic, ip), StatisType.SAVE);
        NavigableMap<Long, StatisData> res2 = producerAllData.getStatisData(new CasKeys("total", topic, ip), StatisType.SEND);
        System.out.println(res1);
        System.out.println(res2);
        System.out.println(producerAllData.getStatisData(new CasKeys("total", topic, ip), StatisType.SAVE));

    }

//    @Test
//    public void testGetAllDelay() {
//
//        //test merge and getDelayForTopic
//        String topic = topics[0];
//        NavigableMap<Long, StatisData> statisData = producerAllData.getStatisDataForTopic(topic, StatisType.SAVE);
//        NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
//        for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
//            result.put(entry.getKey(), entry.getValue().getDelay());
//        }
//        NavigableMap<Long, Long> value1 = producerAllData..getStatisData(new CasKeys("total", topic), StatisType.SAVE);
//        Assert.assertEquals(result, value1);
//
//    }

    @Test
    public void testGetAllQps() {

        //test merge and getQpxForTopic
        String topic = topics[0];
        NavigableMap<Long, StatisData> result1 = producerAllData.getStatisDataForTopic(topic, StatisType.SAVE);
        NavigableMap<Long, StatisData> result2 = producerAllData.getStatisData(new CasKeys("total", topic), StatisType.SAVE);
        Assert.assertEquals(result1.size(), result2.size());
        for (Map.Entry<Long, StatisData> entry : result1.entrySet()) {
            Long key = entry.getKey();
            Statisable.QpxData value1 = new QpxData(entry.getValue());
            Statisable.QpxData value2 = new QpxData(result2.get(key));
            Assert.assertEquals(value1.getQpx(QPX.SECOND), value2.getQpx(QPX.SECOND));
            Assert.assertEquals(value1.getTotal(), value2.getTotal());
        }
        //not change origin data
        NavigableMap<Long, StatisData> result3 = producerAllData.getStatisDataForTopic(topic, StatisType.SAVE);
        for (Map.Entry<Long, StatisData> entry : result1.entrySet()) {
            Long key = entry.getKey();
            Statisable.QpxData value1 = new QpxData(entry.getValue());
            Statisable.QpxData value3 = new QpxData(result3.get(key));
            Assert.assertEquals(value1.getQpx(QPX.SECOND), value3.getQpx(QPX.SECOND));
            Assert.assertEquals(value1.getTotal(), value3.getTotal());
        }
    }

    @Test
    public void testProducerServerData() {

        int totalCount = (int) ((endKey - startKey) / intervalCount);
        for (String topic : topics) {

            NavigableMap<Long, StatisData> statisData = producerAllData.getStatisDataForTopic(topic, StatisType.SAVE);
            NavigableMap<Long, Long> saveDelay = new ConcurrentSkipListMap<Long, Long>();
            NavigableMap<Long, QpxData> saveQpx = new ConcurrentSkipListMap<Long, QpxData>();
            for (Map.Entry<Long, StatisData> entry : statisData.entrySet()) {
                saveQpx.put(entry.getKey(), new QpxData(entry.getValue()));
                saveDelay.put(entry.getKey(), entry.getValue().getDelay());
            }

            expectedDelay(saveDelay, totalCount, avergeDelay);
            expectedQpx(saveQpx, totalCount, qpsPerUnit * ips.length, qpsPerUnit * ips.length
                    * AbstractCollector.SEND_INTERVAL * intervalCount);
        }

        for (Entry<String, NavigableMap<Long, StatisData>> entry : producerAllData.getQpxForServers(StatisType.SAVE)
                .entrySet()) {

            String ip = entry.getKey();
            NavigableMap<Long, StatisData> value = entry.getValue();
            NavigableMap<Long, QpxData> qpxValue = new ConcurrentSkipListMap<Long, QpxData>();
            for (Map.Entry<Long, StatisData> entry1 : value.entrySet()) {
                qpxValue.put(entry1.getKey(), new QpxData(entry1.getValue()));
            }

            if (logger.isInfoEnabled()) {
                logger.info("[testProducerServerData]" + ip + "," + value);
            }
            expectedQpx(qpxValue, totalCount, qpsPerUnit * topics.length, qpsPerUnit * topics.length
                    * AbstractCollector.SEND_INTERVAL * intervalCount);
        }

    }

    protected void expectedQpx(NavigableMap<Long, QpxData> data, int totalCount, Long resultQpx, Long resultTotal) {

        Assert.assertEquals(totalCount, data.size());
        for (QpxData value : data.values()) {

            Assert.assertEquals(resultQpx, value.getQpx(QPX.SECOND));
            Assert.assertEquals(resultTotal, value.getTotal());
        }

    }

    protected void expectedDelay(NavigableMap<Long, Long> data, int totalCount, Long result) {

        Assert.assertEquals(totalCount, data.size());
        for (Long value : data.values()) {

            Assert.assertEquals(result, value);
        }

    }

    public void prepareData(ProducerAllData producerAllData) {

        for (String ip : ips) {

            ProducerMonitorData producerMonitorData = new ProducerMonitorData();
            producerMonitorData.setSwallowServerIp(ip);

            for (Long i = startKey; i <= endKey; i++) {

                producerMonitorData.setCurrentTime(i * AbstractCollector.SEND_INTERVAL * 1000);
                sendData(producerMonitorData, i, ip);
                producerMonitorData.buildTotal();

                try {
                    producerAllData
                            .add(producerMonitorData.getKey(), (ProducerMonitorData) producerMonitorData.clone());
                } catch (CloneNotSupportedException e) {
                    logger.error("[prepareData]", e);
                }
            }

        }

    }

    private ProducerMonitorData sendData(ProducerMonitorData producerMonitorData, Long key, String ip) {

        for (String topic : topics) {

            for (int i = 0; i < qpsPerUnit * AbstractCollector.SEND_INTERVAL; i++) {
                Long current = System.currentTimeMillis();
                producerMonitorData.addData(topic, ip, System.currentTimeMillis(), msgSize, current - avergeDelay, current);
            }
        }

        return producerMonitorData;
    }
}
