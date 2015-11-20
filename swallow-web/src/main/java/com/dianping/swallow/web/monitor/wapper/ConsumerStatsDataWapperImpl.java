package com.dianping.swallow.web.monitor.wapper;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.web.model.stats.*;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 下午3:23:30
 */
@Service("consumerStatsDataWapper")
public class ConsumerStatsDataWapperImpl extends AbstractStatsDataWapper implements ConsumerStatsDataWapper {

    @Autowired
    private ConsumerDataRetriever consumerDataRetriever;

    @Autowired
    private AccumulationRetriever accumulationRetriever;

    @Autowired
    private StatsDataFactory statsDataFactory;

    @Override
    public List<ConsumerServerStatsData> getServerStatsDatas(long timeKey, boolean isTotal) {
        Set<String> serverKeys = consumerDataRetriever.getKeys(new CasKeys());
        if (serverKeys == null) {
            return null;
        }
        Iterator<String> iterator = serverKeys.iterator();
        List<ConsumerServerStatsData> serverStatsDatas = new ArrayList<ConsumerServerStatsData>();
        int index = 0;
        while (iterator.hasNext()) {
            String serverIp = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(serverIp)) {
                continue;
            }

            NavigableMap<Long, QpxData> sendQpx = consumerDataRetriever.getQpsValue(new CasKeys(serverIp), StatisType.SEND);
            NavigableMap<Long, QpxData> ackQpx = consumerDataRetriever.getQpsValue(new CasKeys(serverIp), StatisType.ACK);
            NavigableMap<Long, Long> sendDelay = consumerDataRetriever.getDelayValue(new CasKeys(serverIp), StatisType.SEND);
            NavigableMap<Long, Long> ackDelay = consumerDataRetriever.getDelayValue(new CasKeys(serverIp), StatisType.ACK);
            if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
                    || sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
                continue;
            }
            if (index == 0) {
                Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                index++;
            }

            ConsumerServerStatsData serverStatsData = statsDataFactory.createConsumerServerStatsData();
            serverStatsData.setTimeKey(timeKey);
            serverStatsData.setIp(serverIp);
            QpxData sendQpxValue = sendQpx.get(timeKey);
            if (sendQpxValue != null) {
                serverStatsData.setSendQps(sendQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : sendQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                serverStatsData.setSendQpsTotal(sendQpxValue.getTotal() == null ? 0L : sendQpxValue.getTotal()
                        .longValue());
            }
            QpxData ackQpxValue = ackQpx.get(timeKey);
            if (ackQpxValue != null) {
                serverStatsData.setAckQps(ackQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : ackQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                serverStatsData
                        .setAckQpsTotal(ackQpxValue.getTotal() == null ? 0L : ackQpxValue.getTotal().longValue());
            }

            Long sendDelayValue = sendDelay.get(timeKey);
            if (sendDelayValue != null) {
                serverStatsData.setSendDelay(sendDelayValue.longValue());
            }

            Long ackDelayValue = ackDelay.get(timeKey);
            if (ackDelayValue != null) {
                serverStatsData.setAckDelay(ackDelayValue.longValue());
            }

            serverStatsDatas.add(serverStatsData);
        }
        return serverStatsDatas;
    }

    @Override
    public List<ConsumerIdStatsData> getConsumerIdStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        Iterator<String> iterator = topicKeys.iterator();
        List<ConsumerIdStatsData> consumerIdStatsDataResults = new ArrayList<ConsumerIdStatsData>();
        while (iterator.hasNext()) {
            String topicName = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(topicName)) {
                continue;
            }
            List<ConsumerIdStatsData> consumerIdStatsDatas = getConsumerIdStatsDatas(topicName, timeKey, isTotal);
            if (consumerIdStatsDatas == null) {
                continue;
            }
            consumerIdStatsDataResults.addAll(consumerIdStatsDatas);
        }
        return consumerIdStatsDataResults;
    }

    @Override
    public List<ConsumerIdStatsData> getConsumerIdStatsDatas(String topicName, long timeKey, boolean isTotal) {
        Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
        if (consumerIdKeys == null) {
            return null;
        }
        Iterator<String> iterator = consumerIdKeys.iterator();
        List<ConsumerIdStatsData> consumerIdStatsDatas = new ArrayList<ConsumerIdStatsData>();
        int index = 0;
        while (iterator.hasNext()) {
            String consumerId = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(consumerId)) {
                continue;
            }
            ConsumerIdStatsData consumerIdStatsData = statsDataFactory.createConsumerIdStatsData();
            consumerIdStatsData.setConsumerId(consumerId);
            consumerIdStatsData.setTopicName(topicName);

            NavigableMap<Long, QpxData> sendQpx = consumerDataRetriever.getQpsValue(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND);
            NavigableMap<Long, QpxData> ackQpx = consumerDataRetriever.getQpsValue(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.ACK);
            NavigableMap<Long, Long> sendDelay = consumerDataRetriever.getDelayValue(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND);
            NavigableMap<Long, Long> ackDelay = consumerDataRetriever.getDelayValue(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.ACK);

            if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
                    || sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
                continue;
            }
            if (index == 0) {
                Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                index++;
            }
            consumerIdStatsData.setTimeKey(timeKey);
            QpxData sendQpxValue = sendQpx.get(timeKey);
            if (sendQpxValue != null) {
                consumerIdStatsData.setSendQps(sendQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : sendQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                consumerIdStatsData.setSendQpsTotal(sendQpxValue.getTotal() == null ? 0L : sendQpxValue.getTotal()
                        .longValue());
            }

            QpxData ackQpxValue = ackQpx.get(timeKey);
            if (ackQpxValue != null) {
                consumerIdStatsData.setAckQps(ackQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : ackQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                consumerIdStatsData.setAckQpsTotal(ackQpxValue.getTotal() == null ? 0L : ackQpxValue.getTotal()
                        .longValue());
            }

            Long sendDelayValue = sendDelay.get(timeKey);
            if (sendDelayValue != null) {
                consumerIdStatsData.setSendDelay(sendDelayValue.longValue());
            }

            Long ackDelayValue = ackDelay.get(timeKey);
            if (ackDelayValue != null) {
                consumerIdStatsData.setAckDelay(ackDelayValue.longValue());
            }

            consumerIdStatsData.setAccumulation(getConsumerIdAccumulation(topicName, consumerId, timeKey));
            consumerIdStatsDatas.add(consumerIdStatsData);

        }
        return consumerIdStatsDatas;
    }

    @Override
    public List<ConsumerIpStatsData> getIpStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        Iterator<String> iterator = topicKeys.iterator();
        List<ConsumerIpStatsData> ipStatsDataResults = new ArrayList<ConsumerIpStatsData>();
        while (iterator.hasNext()) {
            String topicName = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(topicName)) {
                continue;
            }
            List<ConsumerIpStatsData> ipStatsDatas = getIpStatsDatas(topicName, timeKey, isTotal);
            if (ipStatsDatas != null) {
                ipStatsDataResults.addAll(ipStatsDatas);
            }
        }
        return ipStatsDataResults;
    }

    @Override
    public List<ConsumerIpStatsData> getIpStatsDatas(String topicName, long timeKey, boolean isTotal) {
        Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
        if (consumerIdKeys == null) {
            return null;
        }
        Iterator<String> iterator = consumerIdKeys.iterator();
        List<ConsumerIpStatsData> ipStatsDatasResults = new ArrayList<ConsumerIpStatsData>();
        while (iterator.hasNext()) {
            String consumerId = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(consumerId)) {
                continue;
            }
            List<ConsumerIpStatsData> ipStatsDatas = getIpStatsDatas(topicName, consumerId, timeKey, isTotal);
            if (ipStatsDatas != null) {
                ipStatsDatasResults.addAll(ipStatsDatas);
            }
        }
        return ipStatsDatasResults;
    }

    @Override
    public List<ConsumerIpStatsData> getIpStatsDatas(String topicName, String consumerId, long timeKey, boolean isTotal) {
        Set<String> ipKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId));
        if (ipKeys == null) {
            return null;
        }
        int index = 0;
        List<ConsumerIpStatsData> ipStatsDatas = new ArrayList<ConsumerIpStatsData>();
        for (String ip : ipKeys) {
            if (!isTotal && TOTAL_KEY.equals(ip)) {
                continue;
            }
            NavigableMap<Long, QpxData> sendQpx = consumerDataRetriever.getQpsValue(new CasKeys(
                    TOTAL_KEY, topicName, consumerId, ip), StatisType.SEND);
            NavigableMap<Long, Long> sendDelay = consumerDataRetriever.getDelayValue(new CasKeys(
                    TOTAL_KEY, topicName, consumerId, ip), StatisType.SEND);

            NavigableMap<Long, QpxData> ackQpx = consumerDataRetriever.getQpsValue(new CasKeys(
                    TOTAL_KEY, topicName, consumerId, ip), StatisType.ACK);
            NavigableMap<Long, Long> ackDelay = consumerDataRetriever.getDelayValue(new CasKeys(
                    TOTAL_KEY, topicName, consumerId, ip), StatisType.ACK);

            if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
                    || sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
                continue;
            }
            if (index == 0) {
                Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                index++;
            }

            ConsumerIpStatsData consumerIpStatsData = statsDataFactory.createConsumerIpStatsData();
            consumerIpStatsData.setTopicName(topicName);
            consumerIpStatsData.setConsumerId(consumerId);
            consumerIpStatsData.setIp(ip);
            consumerIpStatsData.setTimeKey(timeKey);
            QpxData sendQpxValue = sendQpx.get(timeKey);

            if (sendQpxValue != null) {
                consumerIpStatsData.setSendQps(sendQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : sendQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                consumerIpStatsData.setSendQpsTotal(sendQpxValue.getTotal() == null ? 0L : sendQpxValue.getTotal()
                        .longValue());
            }

            Long sendDelayValue = sendDelay.get(timeKey);

            if (sendDelayValue != null) {
                consumerIpStatsData.setSendDelay(sendDelayValue.longValue());
            }

            QpxData ackQpxValue = ackQpx.get(timeKey);

            if (ackQpxValue != null) {
                consumerIpStatsData.setAckQps(ackQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : ackQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
                consumerIpStatsData.setAckQpsTotal(ackQpxValue.getTotal() == null ? 0L : ackQpxValue.getTotal()
                        .longValue());
            }
            Long ackDelayValue = ackDelay.get(timeKey);

            if (ackDelayValue != null) {
                consumerIpStatsData.setAckDelay(ackDelayValue.longValue());
            }
            consumerIpStatsData.setAccumulation(0L);
            ipStatsDatas.add(consumerIpStatsData);
        }
        return ipStatsDatas;
    }

    @Override
    public List<ConsumerIpGroupStatsData> getIpGroupStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        Iterator<String> iterator = topicKeys.iterator();
        List<ConsumerIpGroupStatsData> ipGroupStatsDataResults = new ArrayList<ConsumerIpGroupStatsData>();
        while (iterator.hasNext()) {
            String topicName = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(topicName)) {
                continue;
            }
            List<ConsumerIpGroupStatsData> ipGroupStatsDatas = getIpGroupStatsDatas(topicName, timeKey, isTotal);
            if (ipGroupStatsDatas != null) {
                ipGroupStatsDataResults.addAll(ipGroupStatsDatas);
            }
        }
        return ipGroupStatsDataResults;
    }

    @Override
    public List<ConsumerIpGroupStatsData> getIpGroupStatsDatas(String topicName, long timeKey, boolean isTotal) {
        Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
        if (consumerIdKeys == null) {
            return null;
        }
        Iterator<String> iterator = consumerIdKeys.iterator();
        List<ConsumerIpGroupStatsData> ipGroupStatsDatas = new ArrayList<ConsumerIpGroupStatsData>();
        while (iterator.hasNext()) {
            String consumerId = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(consumerId)) {
                continue;
            }
            ConsumerIpGroupStatsData ipGroupStatsData = getIpGroupStatsDatas(topicName, consumerId, timeKey, isTotal);
            ipGroupStatsDatas.add(ipGroupStatsData);
        }

        return ipGroupStatsDatas;
    }

    @Override
    public ConsumerIpGroupStatsData getIpGroupStatsDatas(String topicName, String consumerId, long timeKey,
                                                         boolean isTotal) {
        ConsumerIpGroupStatsData ipGroupStatsData = new ConsumerIpGroupStatsData();
        ipGroupStatsData.setIpStatsDatas(getIpStatsDatas(topicName, consumerId, timeKey, isTotal));
        return ipGroupStatsData;
    }

    private long getConsumerIdAccumulation(String topic, String consumerId, long timeKey) {
        NavigableMap<Long, Long> accumulations = accumulationRetriever.getConsumerIdAccumulation(topic, consumerId);
        if (accumulations != null && !accumulations.isEmpty()) {
            Long accumulation = accumulations.get(timeKey);
            if (accumulation == null) {
                return accumulations.get(accumulations.lastKey()).longValue();
            }
            return accumulation.longValue();
        }
        return 0L;
    }

    @Override
    public ConsumerTopicStatsData getTotalTopicStatsData(long timeKey) {
        ConsumerTopicStatsData consumerTopicStatsData = statsDataFactory.createConsumerTopicStatsData();
        consumerTopicStatsData.setTopicName(TOTAL_KEY);

        NavigableMap<Long, QpxData> sendQpx = consumerDataRetriever.getQpsValue(new CasKeys(TOTAL_KEY, TOTAL_KEY),
                StatisType.SEND);
        NavigableMap<Long, QpxData> ackQpx = consumerDataRetriever.getQpsValue(new CasKeys(TOTAL_KEY, TOTAL_KEY),
                StatisType.ACK);
        NavigableMap<Long, Long> sendDelay = consumerDataRetriever.getDelayValue(new CasKeys(TOTAL_KEY, TOTAL_KEY),
                StatisType.SEND);
        NavigableMap<Long, Long> ackDelay = consumerDataRetriever.getDelayValue(new CasKeys(TOTAL_KEY, TOTAL_KEY),
                StatisType.ACK);

        if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
                || sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
            return null;
        }
        Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
        if (tempKey == null) {
            return null;
        }
        timeKey = tempKey.longValue();
        consumerTopicStatsData.setTimeKey(timeKey);
        QpxData sendQpxValue = sendQpx.get(timeKey);
        if (sendQpxValue != null) {
            consumerTopicStatsData.setSendQps(sendQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : sendQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
            consumerTopicStatsData.setSendQpsTotal(sendQpxValue.getTotal() == null ? 0L : sendQpxValue.getTotal()
                    .longValue());
        }

        QpxData ackQpxValue = ackQpx.get(timeKey);
        if (ackQpxValue != null) {
            consumerTopicStatsData.setAckQps(ackQpxValue.getQpx(DEFAULT_QPX_TYPE) == null ? 0L : ackQpxValue.getQpx(DEFAULT_QPX_TYPE).longValue());
            consumerTopicStatsData.setAckQpsTotal(ackQpxValue.getTotal() == null ? 0L : ackQpxValue.getTotal()
                    .longValue());
        }

        Long sendDelayValue = sendDelay.get(timeKey);
        if (sendDelayValue != null) {
            consumerTopicStatsData.setSendDelay(sendDelayValue.longValue());
        }

        Long ackDelayValue = ackDelay.get(timeKey);
        if (ackDelayValue != null) {
            consumerTopicStatsData.setAckDelay(ackDelayValue.longValue());
        }

        consumerTopicStatsData.setAccumulation(0L);

        return consumerTopicStatsData;
    }

    @Override
    public Set<String> getConsumerIds(String topicName, boolean isTotal) {
        Set<String> consumerIds = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
        if (!isTotal && consumerIds != null) {
            if (consumerIds.contains(TOTAL_KEY)) {
                consumerIds.remove(TOTAL_KEY);
            }
        }
        return consumerIds;
    }

    @Override
    public Set<String> getTopics(boolean isTotal) {
        Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (!isTotal && topicKeys != null) {
            if (topicKeys.contains(TOTAL_KEY)) {
                topicKeys.remove(TOTAL_KEY);
            }
        }
        return topicKeys;
    }

    @Override
    public Set<String> getServerIps(boolean isTotal) {
        Set<String> serverIps = consumerDataRetriever.getKeys(new CasKeys());
        if (!isTotal && serverIps != null) {
            if (serverIps.contains(TOTAL_KEY)) {
                serverIps.remove(TOTAL_KEY);
            }
        }
        return serverIps;
    }

    @Override
    public Set<String> getConsumerIdIps(String topicName, String consumerId, boolean isTotal) {
        Set<String> consumerIdIps = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId),
                StatisType.SEND);
        if (!isTotal && consumerIdIps != null) {
            if (consumerIdIps.contains(TOTAL_KEY)) {
                consumerIdIps.remove(TOTAL_KEY);
            }
        }
        return consumerIdIps;
    }

    @Override
    public Set<String> getTopicIps(String topicName, boolean isTotal) {
        Set<String> consumerIds = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
        if (consumerIds != null) {
            Iterator<String> iterator = consumerIds.iterator();
            Set<String> ips = new HashSet<String>();
            while (iterator.hasNext()) {
                String consumerId = iterator.next();
                Set<String> tempIps = getConsumerIdIps(topicName, consumerId, false);
                ips.addAll(tempIps);
            }
            return ips;
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getIps(boolean isTotal) {
        Set<String> ips = new HashSet<String>();
        Set<String> topics = getTopics(false);
        if (topics != null) {
            for (String topic : topics) {
                Set<String> topicIps = getTopicIps(topic, false);
                if (topicIps != null) {
                    ips.addAll(topicIps);
                }
            }
        }
        Set<String> serverIps = getServerIps(false);
        if (serverIps != null) {
            ips.addAll(serverIps);
        }
        return ips;
    }

}