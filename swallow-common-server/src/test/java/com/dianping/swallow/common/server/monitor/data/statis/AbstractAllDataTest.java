package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.NavigableMap;

/**
 * @author mingdongli
 *         15/10/29 下午1:12
 */
public class AbstractAllDataTest extends AbstractServerDataTest {

    private ProducerAllData producerAllData;

    @Before
    public void beforeProducerServerDataTest() {

        producerAllData = new ProducerAllData();
        prepareData(producerAllData);
        producerAllData.build(QPX.SECOND, startKey, endKey, intervalCount);
    }

    public static void method3(String fileName, String content) {
        RandomAccessFile randomFile = null;
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testRetriever() {

        String server = ips[0];
        String topic = topics[0];
        String ip = ips[0];

        method3("/Users/mingdongli/tmp/producer/all.txt", producerAllData.toString());
        method3("/Users/mingdongli/tmp/producer/total.txt", producerAllData.getKeys(new CasKeys("total")).toString());

        System.out.println(producerAllData.getKeys(new CasKeys()));
        System.out.println(producerAllData.getKeys(new CasKeys(server)));
        System.out.println(producerAllData.getKeys(new CasKeys(server, topic)));
        System.out.println(producerAllData.getKeys(new CasKeys("total")));
        System.out.println(producerAllData.getKeys(new CasKeys("total",topic)));

        method3("/Users/mingdongli/tmp/producer/topic.txt", producerAllData.getValue(new CasKeys(server)).toString());
        method3("/Users/mingdongli/tmp/producer/cid.txt", producerAllData.getValue(new CasKeys(server, topic)).toString());
        method3("/Users/mingdongli/tmp/producer/topicmerge.txt", producerAllData.getValue(new CasKeys("total")).toString());
        method3("/Users/mingdongli/tmp/producer/cidmerge.txt", producerAllData.getValue(new CasKeys("total", topic)).toString());

        System.out.println(producerAllData.getKeys(new CasKeys(server)));
        System.out.println(producerAllData.getValue(new CasKeys(server, topic, ip)).getClass());

    }

    @Test
    public void testProducerServerData() {

        int totalCount = (int) ((endKey - startKey) / intervalCount);
        for (String topic : topics) {

            NavigableMap<Long, Long> saveDelay = producerAllData.getDelayForTopic(topic, StatisType.SAVE);
            NavigableMap<Long, Statisable.QpxData> saveQpx = producerAllData.getQpxForTopic(topic, StatisType.SAVE);

            expectedDelay(saveDelay, totalCount, avergeDelay);
            expectedQpx(saveQpx, totalCount, qpsPerUnit * ips.length, qpsPerUnit * ips.length
                    * AbstractCollector.SEND_INTERVAL * intervalCount);
        }

        for (Map.Entry<String, NavigableMap<Long, Statisable.QpxData>> entry : producerAllData.getQpxForServers(StatisType.SAVE)
                .entrySet()) {

            String ip = entry.getKey();
            NavigableMap<Long, Statisable.QpxData> value = entry.getValue();

            if (logger.isInfoEnabled()) {
                logger.info("[testProducerServerData]" + ip + "," + value);
            }
            expectedQpx(entry.getValue(), totalCount, qpsPerUnit * topics.length, qpsPerUnit * topics.length
                    * AbstractCollector.SEND_INTERVAL * intervalCount);
        }

    }

    /**
     * @param saveDelay
     * @param totalCount
     * @param avergeDelay2
     */
    protected void expectedQpx(NavigableMap<Long, Statisable.QpxData> data, int totalCount, Long resultQpx, Long resultTotal) {

        Assert.assertEquals(totalCount, data.size());
        for (Statisable.QpxData value : data.values()) {

            Assert.assertEquals(resultQpx, value.getQpx());
            Assert.assertEquals(resultTotal, value.getTotal());
        }

    }

    /**
     * @param saveDelay
     * @param totalCount
     * @param avergeDelay2
     */
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
                producerMonitorData.addData(topic, ip, System.currentTimeMillis(), current - avergeDelay, current);
            }
        }

        return producerMonitorData;
    }
}
