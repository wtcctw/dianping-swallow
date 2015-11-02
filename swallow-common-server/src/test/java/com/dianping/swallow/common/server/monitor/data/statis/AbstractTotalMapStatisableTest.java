package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoMessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertTrue;

/**
 * @author mingdongli
 * 15/10/27 下午3:56
 */

public class  AbstractTotalMapStatisableTest extends AbstractServerDataTest {

    private ConsumerAllData consumerAllData;

    protected String[] consumerIds = { "id1", "id2" };

    protected StatisType[] supportedTypes = new StatisType[] { StatisType.SEND, StatisType.ACK };

    private AtomicLong messageIdGenerator = new AtomicLong();

    @Before
    public void beforeProducerServerDataTest() {

        consumerAllData = new ConsumerAllData();
        prepareData(consumerAllData);
        consumerAllData.build(QPX.SECOND, startKey, endKey, intervalCount);
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
        String consumerId = consumerIds[0];
        String ip = ips[0];

        method3("/Users/mingdongli/tmp/all.txt", consumerAllData.toString());
        method3("/Users/mingdongli/tmp/total.txt", consumerAllData.getKeys(new CasKeys("total")).toString());

        System.out.println(consumerAllData.getKeys(new CasKeys()));
        System.out.println(consumerAllData.getKeys(new CasKeys(server)));
        System.out.println(consumerAllData.getKeys(new CasKeys(server, topic)));
        System.out.println(consumerAllData.getKeys(new CasKeys(server, topic, consumerId)));
        System.out.println(consumerAllData.getKeys(new CasKeys("total")));
        System.out.println(consumerAllData.getKeys(new CasKeys("total",topic)));
        System.out.println(consumerAllData.getKeys(new CasKeys("total",topic, consumerId)));
        ConsumerIdStatisData cisd = (ConsumerIdStatisData) consumerAllData.getValue(new CasKeys(server, topic,
                consumerId));
        method3("/Users/mingdongli/tmp/cids.txt", cisd.toString());

        method3("/Users/mingdongli/tmp/topic.txt", consumerAllData.getValue(new CasKeys(server)).toString());
        method3("/Users/mingdongli/tmp/cid.txt", consumerAllData.getValue(new CasKeys(server, topic)).toString());
        method3("/Users/mingdongli/tmp/ip.txt", consumerAllData.getValue(new CasKeys(server, topic, consumerId)).toString());
        method3("/Users/mingdongli/tmp/iplevel.txt", consumerAllData.getValue(new CasKeys(server, topic, consumerId, ip)).toString());
        method3("/Users/mingdongli/tmp/topicmerge.txt", consumerAllData.getValue(new CasKeys("total")).toString());
        method3("/Users/mingdongli/tmp/cidmerge.txt", consumerAllData.getValue(new CasKeys("total", topic)).toString());
        method3("/Users/mingdongli/tmp/ipmerge.txt", consumerAllData.getValue(new CasKeys("total", topic, consumerId)).toString());
    }

    @Test
    public void testConsumerServerData() {

        int totalCount = (int) ((endKey - startKey) / intervalCount);

        for (String topic : topics) {
            for (StatisType type : supportedTypes) {

                if (logger.isInfoEnabled()) {
                    logger.info("[testConsumerServerData]" + type);
                }

                NavigableMap<Long, Long> delays = consumerAllData.getDelayForTopic(topic, type);
                NavigableMap<Long, QpxData> qpxs = consumerAllData.getQpxForTopic(topic, type);

                expectedDelay(delays, totalCount, avergeDelay);
                expectedQpx(qpxs, totalCount, qpsPerUnit * ips.length * consumerIds.length, qpsPerUnit * ips.length
                        * consumerIds.length * intervalCount * AbstractCollector.SEND_INTERVAL);

                Map<String, NavigableMap<Long, Long>> allDelay = consumerAllData.getDelayForAllConsumerId(topic, type,
                        false);
                Map<String, NavigableMap<Long, QpxData>> allQpx = consumerAllData.getQpxForAllConsumerId(topic, type,
                        false);

                Assert.assertEquals(consumerIds.length, allDelay.size());
                Assert.assertEquals(consumerIds.length, allQpx.size());

                for (Entry<String, NavigableMap<Long, Long>> entry : allDelay.entrySet()) {

                    NavigableMap<Long, Long> consumerDelay = entry.getValue();
                    expectedDelay(consumerDelay, totalCount, avergeDelay);
                }

                for (Entry<String, NavigableMap<Long, QpxData>> entry : allQpx.entrySet()) {

                    NavigableMap<Long, QpxData> consumerQpx = entry.getValue();
                    expectedQpx(consumerQpx, totalCount, qpsPerUnit * ips.length, qpsPerUnit * ips.length
                            * intervalCount * AbstractCollector.SEND_INTERVAL);
                }

            }

        }

    }

    private void expectedDelay(NavigableMap<Long, Long> delays, int totalCount, long avergeDelay) {

        if (logger.isInfoEnabled()) {
            logger.info("[expectedDelay][" + avergeDelay + "]" + delays);
        }

        Assert.assertEquals(totalCount, delays.size());
        for (Long value : delays.values()) {

            Long low = (long) (avergeDelay * 0.8);
            Long high = (long) (avergeDelay * 1.2);

            assertTrue(value >= low && value <= high);
        }
    }

    protected void expectedQpx(NavigableMap<Long, QpxData> data, int totalCount, Long resultQpx, Long resultTotal) {

        if (logger.isInfoEnabled()) {
            logger.info("[expected][" + avergeDelay + "]" + data);
        }

        Assert.assertEquals(totalCount, data.size());
        for (QpxData value : data.values()) {

            Assert.assertEquals(resultQpx, value.getQpx());
            Assert.assertEquals(resultTotal, value.getTotal());
        }
    }

    public void prepareData(ConsumerAllData consumerAllData) {

        for (String ip : ips) {

            ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData();
            consumerMonitorData.setSwallowServerIp(ip);

            for (Long i = startKey; i <= endKey; i++) {

                List<Wrapper> datas = new LinkedList<AbstractTotalMapStatisableTest.Wrapper>();

                consumerMonitorData.setCurrentTime(i * AbstractCollector.SEND_INTERVAL * 1000);

                datas.addAll(sendData(consumerMonitorData, i, ip));

                if (logger.isDebugEnabled()) {
                    logger.debug("[prepareData][total]" + consumerMonitorData);
                }

                System.setProperty(ConsumerIdData.ACK_DELAY_FOR_UNIT_KEY, String.valueOf(avergeDelay));
                ackData(consumerMonitorData, datas);

                consumerMonitorData.buildTotal();

                ConsumerMonitorData copy = null;
                try {
                    copy = (ConsumerMonitorData) consumerMonitorData.clone();
                } catch (CloneNotSupportedException e) {
                    logger.error("[prepareData]", e);
                }

                consumerAllData.add(consumerMonitorData.getKey(), copy);
            }

        }

    }

    private void ackData(ConsumerMonitorData consumerMonitorData, List<Wrapper> datas) {

        for (Wrapper wrapper : datas) {

            consumerMonitorData.addAckData(wrapper.info, wrapper.ip, wrapper.message);
        }
    }

    private List<Wrapper> sendData(ConsumerMonitorData consumerMonitorData, Long key, String ip) {

        List<Wrapper> wrappers = new LinkedList<Wrapper>();

        for (String topic : topics) {

            for (String consumerId : consumerIds) {

                ConsumerInfo consumerInfo = new ConsumerInfo(consumerId, Destination.topic(topic),
                        ConsumerType.DURABLE_AT_LEAST_ONCE);

                for (int i = 0; i < qpsPerUnit * AbstractCollector.SEND_INTERVAL; i++) {

                    SwallowMessage message = createMessage();
                    message.setMessageId(messageIdGenerator.incrementAndGet());
                    message.getInternalProperties().put(MongoMessageDAO.SAVE_TIME,
                            String.valueOf(System.currentTimeMillis() - avergeDelay));
                    wrappers.add(new Wrapper(consumerInfo, ip, message));

                    consumerMonitorData.addSendData(consumerInfo, ip, message);
                }
            }
        }

        return wrappers;
    }

    public static class Wrapper {

        protected ConsumerInfo info;
        protected String ip;
        protected SwallowMessage message;

        public Wrapper(ConsumerInfo info, String ip, SwallowMessage message) {
            this.info = info;
            this.ip = ip;
            this.message = message;
        }
    }
}
