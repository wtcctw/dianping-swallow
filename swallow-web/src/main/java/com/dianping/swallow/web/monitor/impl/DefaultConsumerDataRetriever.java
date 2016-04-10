package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.statis.*;
import com.dianping.swallow.common.server.monitor.data.structure.*;
import com.dianping.swallow.web.container.IpResourceContainer;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.*;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午11:04:09
 */
@Component
public class DefaultConsumerDataRetriever
        extends
        AbstractMonitorDataRetriever<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData>
        implements ConsumerDataRetriever {

    public static final String CAT_TYPE = "ConsumerDataRetriever";

    public static final String FACTORY_NAME = "ConsumerOrderInDb";

    @Autowired
    private ConsumerServerStatsDataService cServerStatsDataService;

    @Autowired
    private ConsumerTopicStatsDataService cTopicStatsDataService;

    @Autowired
    private ConsumerIdStatsDataService consumerIdStatsDataService;

    @Autowired
    private AccumulationRetriever accumulationRetriever;

    @Autowired
    private ResourceContainer resourceContainer;

    @Autowired
    private IpResourceContainer ipResourceContainer;

    @Override
    public boolean dataExistInMemory(CasKeys keys, long start, long end) {
        return dataExistInMemory(keys, StatisType.SEND, start, end);
    }

    @Override
    public List<OrderStatsData> getOrderForAllConsumerId(int size) {
        return getOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public List<OrderStatsData> getOrderForAllConsumerId(int size, long start, long end) {

        if (dataExistInMemory(new CasKeys(TOTAL_KEY, TOTAL_KEY), start, end)) {
            return getOrderInMemory(size, start, end);
        }

        return getOrderInDb(size, start, end);
    }

    public List<OrderStatsData> getOrderInMemory(int size, long start, long end) {
        OrderStatsData accuStatsData = accumulationRetriever.getAccuOrderForAllConsumerId(size, start, end);
        OrderStatsData qpxSendStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.SEND), start, end);
        OrderStatsData qpxAckStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.ACK), start, end);
        OrderStatsData delaySendStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.SEND), start, end);
        OrderStatsData delayAckStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.ACK), start, end);
        ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
        Set<String> topics = retriever.getTopics(false);
        if (topics == null) {
            return null;
        }
        Iterator<String> iterator = topics.iterator();
        long fromKey = getKey(start);
        long toKey = getKey(end);
        while (iterator.hasNext()) {
            String topicName = iterator.next();
            if (TOTAL_KEY.equals(topicName)) {
                continue;
            }
            Set<String> consumerIds = retriever.getConsumerIds(topicName, false);
            if (consumerIds == null || consumerIds.isEmpty()) {
                continue;
            }
            Iterator<String> itConsumerId = consumerIds.iterator();
            while (itConsumerId.hasNext()) {
                String consumerId = itConsumerId.next();
                NavigableMap<Long, StatisData> lastSendDatas = statis.getLessThanData(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND, toKey);
                NavigableMap<Long, StatisData> firstSendDatas = statis.getMoreThanData(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND, fromKey);
                NavigableMap<Long, StatisData> lastAckDatas = statis.getLessThanData(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.ACK, toKey);
                NavigableMap<Long, StatisData> firstAckDatas = statis.getMoreThanData(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.ACK, fromKey);
                if (lastSendDatas != null && !lastSendDatas.isEmpty() && firstSendDatas != null && !firstSendDatas.isEmpty()) {
                    StatisData lastData = lastSendDatas.lastEntry().getValue();
                    StatisData firstData = firstSendDatas.lastEntry().getValue();
                    long subTotalDelay = lastData.getTotalDelay() - firstData.getTotalDelay();
                    long subTotalCount = lastData.getTotalCount() - firstData.getTotalCount();
                    delaySendStatsData.add(new OrderEntity(topicName, consumerId, subTotalDelay, subTotalCount));
                    qpxSendStatsData.add(new OrderEntity(topicName, consumerId, subTotalCount, getQpsSampleCount(start, end)));
                }
                if (lastAckDatas != null && !lastAckDatas.isEmpty() && firstAckDatas != null && !firstAckDatas.isEmpty()) {
                    StatisData lastData = lastAckDatas.lastEntry().getValue();
                    StatisData firstData = firstAckDatas.lastEntry().getValue();
                    long subTotalDelay = lastData.getTotalDelay() - firstData.getTotalDelay();
                    long subTotalCount = lastData.getTotalCount() - firstData.getTotalCount();
                    delayAckStatsData.add(new OrderEntity(topicName, consumerId, subTotalDelay, subTotalCount));
                    qpxAckStatsData.add(new OrderEntity(topicName, consumerId, subTotalCount, getQpsSampleCount(start, end)));
                }
            }
        }
        List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
        orderStatsDatas.add(delaySendStatsData);
        orderStatsDatas.add(delayAckStatsData);
        orderStatsDatas.add(qpxSendStatsData);
        orderStatsDatas.add(qpxAckStatsData);
        orderStatsDatas.add(accuStatsData);
        return orderStatsDatas;
    }

    public List<OrderStatsData> getOrderInDb(int size, long start, long end) {
        long fromKey = getKey(start);
        long toKey = getKey(end);
        OrderStatsData qpxSendStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.SEND), start, end);
        OrderStatsData qpxAckStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.ACK), start, end);
        OrderStatsData delaySendStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.SEND), start, end);
        OrderStatsData delayAckStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.ACK), start, end);
        OrderStatsData accuStatsData = new OrderStatsData(size, new ConsumerStatsDataDesc(TOTAL_KEY, StatisDetailType.ACCUMULATION), start, end);
        List<ConsumerIdResource> consumerIdResources = resourceContainer.findConsumerIdResources(false);
        if (consumerIdResources != null && consumerIdResources.size() > 0) {
            QueryQrderTask queryQrderTask = new QueryQrderTask();
            for (ConsumerIdResource consumerIdResource : consumerIdResources) {
                String topicName = consumerIdResource.getTopic();
                String consumerId = consumerIdResource.getConsumerId();
                if (TOTAL_KEY.equals(topicName) || TOTAL_KEY.equals(consumerId)) {
                    continue;
                }
                queryQrderTask.submit(new QueryOrderParam(topicName, consumerId, fromKey, toKey, delaySendStatsData,
                        delayAckStatsData, qpxSendStatsData, qpxAckStatsData, accuStatsData));
            }
            queryQrderTask.await();
        }
        List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
        orderStatsDatas.add(delaySendStatsData);
        orderStatsDatas.add(delayAckStatsData);
        orderStatsDatas.add(qpxSendStatsData);
        orderStatsDatas.add(qpxAckStatsData);
        orderStatsDatas.add(accuStatsData);
        return orderStatsDatas;
    }

    @Override
    public List<ConsumerDataPair> getDelayForAllConsumerId(String topic, long start, long end) {

        ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
        Map<String, NavigableMap<Long, Long>> sendDelays = null;
        Map<String, NavigableMap<Long, Long>> ackDelays = null;
        List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
        long startKey = getKey(start);
        long endKey = getKey(end);
        boolean isTotal = false;
        if (MonitorData.TOTAL_KEY.equals(topic)) {
            isTotal = true;
        }
        if (dataExistInMemory(new CasKeys(TOTAL_KEY, topic), start, end)) {
            sendDelays = retriever.getDelayForAllConsumerId(topic, StatisType.SEND, isTotal);
            ackDelays = retriever.getDelayForAllConsumerId(topic, StatisType.ACK, isTotal);
            if (sendDelays != null) {

                for (Entry<String, NavigableMap<Long, Long>> entry : sendDelays.entrySet()) {

                    String consumerId = entry.getKey();
                    NavigableMap<Long, Long> send = entry.getValue();
                    NavigableMap<Long, Long> ack = ackDelays.get(consumerId);
                    if (send == null) {
                        continue;
                    }
                    send = send.subMap(startKey, true, endKey, true);
                    ack = ack.subMap(startKey, true, endKey, true);
                    send = fillStatsData(send, startKey, endKey);
                    ack = fillStatsData(ack, startKey, endKey);
                    StatsData sendStatis = createStatsData(
                            createConsumerIdDelayDesc(topic, consumerId, StatisType.SEND), send, start, end);
                    StatsData ackStatis = createStatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.ACK),
                            ack, start, end);
                    result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
                }
            }
        } else {
            Map<String, StatsDataMapPair> statsDataResults = getTopicDelayInDb(topic, start, end);
            if (statsDataResults != null && !statsDataResults.isEmpty()) {
                for (Map.Entry<String, StatsDataMapPair> statsDataResult : statsDataResults.entrySet()) {
                    if (!isTotal && MonitorData.TOTAL_KEY.equals(statsDataResult.getKey())) {
                        continue;
                    }
                    StatsDataMapPair statsDataMapPair = statsDataResult.getValue();
                    NavigableMap<Long, Long> sendRawData = null;
                    NavigableMap<Long, Long> ackRawData = null;
                    if (statsDataMapPair != null) {
                        sendRawData = statsDataMapPair.getSendStatsData();
                        ackRawData = statsDataMapPair.getAckStatsData();
                    }
                    sendRawData = fillStatsData(sendRawData, startKey, endKey);
                    StatsData sendStatis = createStatsData(
                            createConsumerIdDelayDesc(topic, statsDataResult.getKey(), StatisType.SEND), sendRawData,
                            start, end);
                    ackRawData = fillStatsData(ackRawData, startKey, endKey);
                    StatsData ackStatis = createStatsData(
                            createConsumerIdDelayDesc(topic, statsDataResult.getKey(), StatisType.ACK), ackRawData,
                            start, end);
                    result.add(new ConsumerDataPair(statsDataResult.getKey(), sendStatis, ackStatis));
                }
            }
        }

        return result;
    }

    protected Map<String, StatsDataMapPair> getTopicDelayInDb(String topic, long start, long end) {
        long startKey = getKey(start);
        long endKey = getKey(end);
        Map<String, StatsDataMapPair> statsDataResults = new HashMap<String, StatsDataMapPair>();
        if (MonitorData.TOTAL_KEY.equals(topic)) {
            StatsDataMapPair statsDataResult = cTopicStatsDataService.findSectionDelayData(topic, startKey, endKey);
            statsDataResults.put(topic, statsDataResult);
        } else {
            statsDataResults = consumerIdStatsDataService.findSectionDelayData(topic, startKey, endKey);
        }
        return statsDataResults;
    }

    protected ConsumerDataPair getIpDelayInMemory(String topic, String consumerId, String ip, long start, long end) {
        NavigableMap<Long, Long> sendDelays = getDelayValue(new CasKeys(TOTAL_KEY, topic, consumerId, ip), StatisType.SEND);
        NavigableMap<Long, Long> ackDelays = getDelayValue(new CasKeys(TOTAL_KEY, topic, consumerId, ip), StatisType.ACK);
        long startKey = getKey(start);
        long endKey = getKey(end);
        sendDelays = fillStatsData(sendDelays, startKey, endKey);
        ackDelays = fillStatsData(ackDelays, startKey, endKey);
        StatsData sendStatis = createStatsData(
                createConsumerIdDelayDesc(topic, consumerId, StatisType.SEND), sendDelays, start, end);
        StatsData ackStatis = createStatsData(
                createConsumerIdDelayDesc(topic, consumerId, StatisType.ACK), ackDelays, start, end);
        ConsumerDataPair result = new ConsumerDataPair(consumerId, sendStatis, ackStatis);
        return result;
    }

    protected ConsumerDataPair getIpDelay(String topic, String consumerId, String ip, long start, long end) {
        if (dataExistInMemory(new CasKeys(TOTAL_KEY, topic, consumerId, ip), start, end)) {
            return getIpDelayInMemory(topic, consumerId, ip, start, end);
        }
        return getIpDelayInMemory(topic, consumerId, ip, start, end);
    }


    public Map<String, ConsumerDataPair> getAllIpDelay(String topic, String consumerId, long start, long end) {
        Map<String, ConsumerDataPair> statsDatas = new HashMap<String, ConsumerDataPair>();
        Set<String> keys = statis.getKeys(new CasKeys(TOTAL_KEY, topic, consumerId), StatisType.SEND);
        if (keys != null) {
            for (String key : keys) {
                if (TOTAL_KEY.equals(key)) {
                    continue;
                }
                statsDatas.put(key, getIpDelay(topic, consumerId, key, start, end));
            }
            return statsDatas;
        }
        return null;
    }

    @Override
    public Map<String, ConsumerDataPair> getAllIpDelay(String topic, String consumerId) {
        return getAllIpDelay(topic, consumerId, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public List<IpStatsData> getAllIpDelayList(String topic, String consumerId, long start, long end) {
        Map<String, ConsumerDataPair> statsDatas = getAllIpDelay(topic, consumerId, start, end);
        return convertToOrderList(statsDatas);
    }

    @Override
    public List<IpStatsData> getAllIpDelayList(String topic, String consumerId) {
        return getAllIpDelayList(topic, consumerId, getDefaultStart(), getDefaultEnd());
    }

    private List<IpStatsData> convertToOrderList(Map<String, ConsumerDataPair> statsDatas) {
        if (statsDatas != null || !statsDatas.isEmpty()) {
            List<IpStatsData> ipStatsDatas = new ArrayList<IpStatsData>();
            for (Map.Entry<String, ConsumerDataPair> entry : statsDatas.entrySet()) {
                String ip = entry.getKey();
                String appName = ipResourceContainer.getApplicationName(ip);
                if (appName == null) {
                    appName = StringUtils.EMPTY;
                }
                ConsumerDataPair consumerData = entry.getValue();
                ipStatsDatas.add(new IpStatsData(appName, ip, new ConsumerStatsData(consumerData.getConsumerId(),
                        consumerData.getSendData(), consumerData.getAckData())));
            }
            Collections.sort(ipStatsDatas);
            Collections.reverse(ipStatsDatas);
            return ipStatsDatas;
        }
        return null;
    }

    @Override
    public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, long start, long end) {

        ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
        Map<String, NavigableMap<Long, StatisData>> sendQpxs = null;
        Map<String, NavigableMap<Long, StatisData>> ackQpxs = null;
        List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
        long startKey = getKey(start);
        long endKey = getKey(end);
        boolean isTotal = false;
        if (MonitorData.TOTAL_KEY.equals(topic)) {
            isTotal = true;
        }
        if (dataExistInMemory(new CasKeys(TOTAL_KEY, topic), start, end)) {
            sendQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.SEND, isTotal);
            ackQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.ACK, isTotal);
            if (sendQpxs != null) {
                for (Entry<String, NavigableMap<Long, StatisData>> entry : sendQpxs.entrySet()) {

                    String consumerId = entry.getKey();
                    NavigableMap<Long, Long> send = convertData(entry.getValue(), StatisFunctionType.QPX);
                    NavigableMap<Long, Long> ack = convertData(ackQpxs.get(consumerId), StatisFunctionType.QPX);
                    if (send == null) {
                        continue;
                    }
                    send = send.subMap(startKey, true, endKey, true);
                    ack = ack.subMap(startKey, true, endKey, true);
                    send = fillStatsData(send, startKey, endKey);
                    ack = fillStatsData(ack, startKey, endKey);
                    StatsData sendStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.SEND),
                            send, start, end);
                    StatsData ackStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.ACK),
                            ack, start, end);

                    result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
                }
            }
        } else {
            Map<String, StatsDataMapPair> statsDataResults = getTopicQpxInDb(topic, qpx, start, end);
            if (statsDataResults != null && !statsDataResults.isEmpty()) {
                for (Map.Entry<String, StatsDataMapPair> statsDataResult : statsDataResults.entrySet()) {
                    if (!isTotal && MonitorData.TOTAL_KEY.equals(statsDataResult.getKey())) {
                        continue;
                    }
                    StatsDataMapPair statsDataMapPair = statsDataResult.getValue();
                    NavigableMap<Long, Long> sendRawData = null;
                    NavigableMap<Long, Long> ackRawData = null;
                    if (statsDataMapPair != null) {
                        sendRawData = statsDataMapPair.getSendStatsData();
                        ackRawData = statsDataMapPair.getAckStatsData();
                    }
                    sendRawData = fillStatsData(sendRawData, startKey, endKey);
                    StatsData sendStatis = createStatsData(
                            createConsumerIdQpxDesc(topic, statsDataResult.getKey(), StatisType.SEND), sendRawData,
                            start, end);

                    ackRawData = fillStatsData(ackRawData, startKey, endKey);
                    StatsData ackStatis = createStatsData(
                            createConsumerIdQpxDesc(topic, statsDataResult.getKey(), StatisType.ACK), ackRawData,
                            start, end);
                    result.add(new ConsumerDataPair(statsDataResult.getKey(), sendStatis, ackStatis));
                }
            }
        }

        return result;
    }

    protected Map<String, StatsDataMapPair> getTopicQpxInDb(String topic, QPX qpx, long start, long end) {
        long startKey = getKey(start);
        long endKey = getKey(end);
        Map<String, StatsDataMapPair> statsDataResults = null;
        if (MonitorData.TOTAL_KEY.equals(topic)) {
            StatsDataMapPair statsDataResult = cTopicStatsDataService.findSectionQpsData(MonitorData.TOTAL_KEY,
                    startKey, endKey);
            statsDataResults = new HashMap<String, StatsDataMapPair>();
            statsDataResults.put(topic, statsDataResult);
        } else {
            statsDataResults = consumerIdStatsDataService.findSectionQpsData(topic, startKey, endKey);
        }
        return statsDataResults;
    }

    protected ConsumerDataPair getIpQpxInMemory(String topic, String consumerId, String ip, long start, long end) {
        ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
        NavigableMap<Long, StatisData> sendQpxs = retriever.getStatisData(new CasKeys(TOTAL_KEY, topic, consumerId, ip), StatisType.SEND);
        NavigableMap<Long, StatisData> ackQpxs = retriever.getStatisData(new CasKeys(TOTAL_KEY, topic, consumerId, ip), StatisType.ACK);
        NavigableMap<Long, Long> send = convertData(sendQpxs, StatisFunctionType.QPX);
        NavigableMap<Long, Long> ack = convertData(ackQpxs, StatisFunctionType.QPX);
        long startKey = getKey(start);
        long endKey = getKey(end);
        send = fillStatsData(send, startKey, endKey);
        ack = fillStatsData(ack, startKey, endKey);
        StatsData sendStats = createStatsData(
                createConsumerIdQpxDesc(topic, consumerId, StatisType.SEND), send, start, end);
        StatsData ackStats = createStatsData(
                createConsumerIdQpxDesc(topic, consumerId, StatisType.ACK), ack, start, end);
        ConsumerDataPair result = new ConsumerDataPair(consumerId, sendStats, ackStats);
        return result;
    }

    protected ConsumerDataPair getIpQpx(String topic, String consumerId, String ip, long start, long end) {
        if (dataExistInMemory(new CasKeys(TOTAL_KEY, topic, consumerId, ip), start, end)) {
            return getIpQpxInMemory(topic, consumerId, ip, start, end);
        }
        return getIpQpxInMemory(topic, consumerId, ip, start, end);
    }

    @Override
    public Map<String, ConsumerDataPair> getAllIpQpx(String topic, String consumerId, long start, long end) {
        Map<String, ConsumerDataPair> statsDatas = new HashMap<String, ConsumerDataPair>();
        Set<String> keys = statis.getKeys(new CasKeys(TOTAL_KEY, topic, consumerId), StatisType.SEND);
        if (keys != null) {
            for (String key : keys) {
                if (TOTAL_KEY.equals(key)) {
                    continue;
                }
                statsDatas.put(key, getIpQpx(topic, consumerId, key, start, end));
            }
            return statsDatas;
        }
        return null;
    }

    @Override
    public Map<String, ConsumerDataPair> getAllIpQpx(String topic, String consumerId) {
        return getAllIpQpx(topic, consumerId, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public List<IpStatsData> getAllIpQpxList(String topic, String consumerId, long start, long end) {
        Map<String, ConsumerDataPair> statsDatas = getAllIpQpx(topic, consumerId, start, end);
        return convertToOrderList(statsDatas);
    }

    @Override
    public List<IpStatsData> getAllIpQpxList(String topic, String consumerId) {
        return getAllIpQpxList(topic, consumerId, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public Map<String, ConsumerDataPair> getServerQpx(QPX qpx, long start, long end) {

        Map<String, StatsData> sendQpxs = null;

        Map<String, StatsData> ackQpxs = null;

        if (dataExistInMemory(new CasKeys(TOTAL_KEY, TOTAL_KEY), start, end)) {
            sendQpxs = getServerQpxInMemory(qpx, StatisType.SEND, start, end);
            ackQpxs = getServerQpxInMemory(qpx, StatisType.ACK, start, end);
        } else {
            StatsDataPair statsDataPair = getServerQpxInDb(qpx, start, end);
            sendQpxs = statsDataPair.getSendStatsDatas();
            ackQpxs = statsDataPair.getAckStatsDatas();
        }

        Map<String, ConsumerDataPair> result = new HashMap<String, ConsumerDataRetriever.ConsumerDataPair>();
        for (Entry<String, StatsData> entry : sendQpxs.entrySet()) {

            String serverIp = entry.getKey();
            StatsData sendQpx = entry.getValue();
            StatsData ackQpx = ackQpxs.get(serverIp);
            result.put(serverIp, new ConsumerDataPair(getConsumerIdSubTitle(MonitorData.TOTAL_KEY), sendQpx, ackQpx));
        }

        return result;
    }

    protected StatsDataPair getServerQpxInDb(QPX qpx, long start, long end) {
        StatsDataPair statsDataPair = new StatsDataPair();
        Map<String, StatsData> sendStatsDatas = new HashMap<String, StatsData>();
        Map<String, StatsData> ackStatsDatas = new HashMap<String, StatsData>();
        long startKey = getCeilingTime(start);
        long endKey = getCeilingTime(end);
        Map<String, StatsDataMapPair> statsDataPairMaps = cServerStatsDataService.findSectionQpsData(startKey, endKey);

        for (Map.Entry<String, StatsDataMapPair> qpsStatsDataMap : statsDataPairMaps.entrySet()) {
            String serverIp = qpsStatsDataMap.getKey();

            if (StringUtils.equals(TOTAL_KEY, serverIp)) {
                continue;
            }

            StatsDataMapPair statsDataMapPair = qpsStatsDataMap.getValue();
            NavigableMap<Long, Long> sendStatsData = null;
            NavigableMap<Long, Long> ackStatsData = null;
            if (statsDataMapPair != null) {
                sendStatsData = statsDataMapPair.getSendStatsData();
                ackStatsData = statsDataMapPair.getAckStatsData();
            }
            sendStatsData = fillStatsData(sendStatsData, startKey, endKey);
            ackStatsData = fillStatsData(ackStatsData, startKey, endKey);
            sendStatsDatas.put(serverIp,
                    createStatsData(createServerQpxDesc(serverIp, StatisType.SEND), sendStatsData, start, end));

            ackStatsDatas.put(serverIp,
                    createStatsData(createServerQpxDesc(serverIp, StatisType.ACK), ackStatsData, start, end));

        }
        statsDataPair.setAckStatsDatas(ackStatsDatas);
        statsDataPair.setSendStatsDatas(sendStatsDatas);
        return statsDataPair;
    }

    private String getConsumerIdSubTitle(String consumerId) {
        if (consumerId.equals(MonitorData.TOTAL_KEY)) {
            return "全局平均";
        }
        return "consumerID:" + consumerId;
    }

    @Override
    public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx) {

        return getQpxForAllConsumerId(topic, qpx, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public Map<String, ConsumerDataPair> getServerQpx(QPX qpx) {

        return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public List<ConsumerDataPair> getDelayForAllConsumerId(String topic) throws Exception {

        return getDelayForAllConsumerId(topic, getDefaultStart(), getDefaultEnd());
    }

    @Override
    protected AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData> createServerStatis() {

        return new ConsumerAllData();
    }

    @Override
    protected StatsDataDesc createDelayDesc(String topic, StatisType type) {

        return new ConsumerStatsDataDesc(topic, type.getDelayDetailType());
    }

    @Override
    protected StatsDataDesc createQpxDesc(String topic, StatisType type) {

        return new ConsumerStatsDataDesc(topic, type.getQpxDetailType());
    }

    @Override
    protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

        return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
    }

    @Override
    protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {

        return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
    }

    protected StatsDataDesc createConsumerIdDelayDesc(String topic, String consumerId, StatisType type) {

        return new ConsumerStatsDataDesc(topic, consumerId, type.getDelayDetailType());
    }

    protected StatsDataDesc createConsumerIdQpxDesc(String topic, String consumerId, StatisType type) {

        return new ConsumerStatsDataDesc(topic, consumerId, type.getQpxDetailType());
    }

    @Override
    public Map<String, Set<String>> getAllTopics() {

        ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;

        return retriever.getAllTopics();
    }

    private ConsumerIdStatsData getPreConsumerIdStatsData(String topicName, String consumerId, long startKey,
                                                          long endKey) {
        ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
                topicName, consumerId, startKey, endKey, true);
        if (consumerIdStatsData != null) {
            return consumerIdStatsData;
        }
        return new ConsumerIdStatsData();
    }

    private ConsumerIdStatsData getPostConsumerIdStatsData(String topicName, String consumerId, long startKey,
                                                           long endKey) {
        ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
                topicName, consumerId, startKey, endKey, false);
        if (consumerIdStatsData != null) {
            return consumerIdStatsData;
        }
        return new ConsumerIdStatsData();
    }

    public static class StatsDataPair {

        private Map<String, StatsData> sendStatsDatas;

        private Map<String, StatsData> ackStatsDatas;

        public Map<String, StatsData> getSendStatsDatas() {
            return sendStatsDatas;
        }

        public void setSendStatsDatas(Map<String, StatsData> sendStatsDatas) {
            this.sendStatsDatas = sendStatsDatas;
        }

        public Map<String, StatsData> getAckStatsDatas() {
            return ackStatsDatas;
        }

        public void setAckStatsDatas(Map<String, StatsData> ackStatsDatas) {
            this.ackStatsDatas = ackStatsDatas;
        }

    }

    private class QueryQrderTask {

        private static final int poolSize = CommonUtils.DEFAULT_CPU_COUNT * 6;

        private static final int MAX_WAIT_TIME = 60;

        private ExecutorService executorService = Executors.newFixedThreadPool(poolSize,
                ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

        public QueryQrderTask() {
            logger.info("[QueryQrderTask] poolSize {} .", poolSize);
        }

        public void submit(final QueryOrderParam orderParam) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    queryOrder(orderParam);
                }
            });
        }

        private void queryOrder(QueryOrderParam orderParam) {
            String topicName = orderParam.getTopicName();
            String consumerId = orderParam.getConsumerId();
            ConsumerIdStatsData preStatsData = getPreConsumerIdStatsData(topicName, consumerId,
                    orderParam.getFromKey(), orderParam.getToKey());
            ConsumerIdStatsData postStatsData = getPostConsumerIdStatsData(topicName, consumerId,
                    orderParam.getFromKey(), orderParam.getToKey());
            long start = orderParam.getQpxSendStatsData().getStart();
            long end = orderParam.getQpxSendStatsData().getEnd();

            long totalSendQps = postStatsData.getTotalSendQps() - preStatsData.getTotalSendQps();
            orderParam.getQpxSendStatsData().add(
                    new OrderEntity(topicName, consumerId, totalSendQps, getQpsSampleCount(start, end)));

            long totalAckQps = postStatsData.getTotalAckQps() - preStatsData.getTotalAckQps();
            orderParam.getQpxAckStatsData().add(
                    new OrderEntity(topicName, consumerId, totalAckQps, getQpsSampleCount(start, end)));

            orderParam.getDelaySendStatsData().add(
                    new OrderEntity(topicName, consumerId, postStatsData.getTotalSendDelay()
                            - preStatsData.getTotalSendDelay(), totalSendQps));

            orderParam.getDelayAckStatsData().add(
                    new OrderEntity(topicName, consumerId, postStatsData.getTotalAckDelay()
                            - preStatsData.getTotalAckDelay(), totalAckQps));

            orderParam.getAccuStatsData().add(
                    new OrderEntity(topicName, consumerId, postStatsData.getTotalAccumulation()
                            - preStatsData.getTotalAccumulation(), getOtherSampleCount(start, end)));
        }

        public void await() {

            executorService.shutdown();
            try {
                executorService.awaitTermination(MAX_WAIT_TIME, TimeUnit.SECONDS);
                executorService.shutdownNow();
                logger.info("[await] QueryQrderTask is over.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class QueryOrderParam {

        private String topicName;

        private String consumerId;

        private long fromKey;

        private long toKey;

        private OrderStatsData delaySendStatsData;

        private OrderStatsData delayAckStatsData;

        private OrderStatsData qpxSendStatsData;

        private OrderStatsData qpxAckStatsData;

        private OrderStatsData accuStatsData;

        public QueryOrderParam(String topicName, String consumerId, long fromKey, long toKey,
                               OrderStatsData delaySendStatsData, OrderStatsData delayAckStatsData, OrderStatsData qpxSendStatsData,
                               OrderStatsData qpxAckStatsData, OrderStatsData accuStatsData) {
            this.topicName = topicName;
            this.consumerId = consumerId;
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.setDelaySendStatsData(delaySendStatsData);
            this.setDelayAckStatsData(delayAckStatsData);
            this.setQpxSendStatsData(qpxSendStatsData);
            this.setQpxAckStatsData(qpxAckStatsData);
            this.setAccuStatsData(accuStatsData);
        }

        public String getTopicName() {
            return topicName;
        }

        public long getFromKey() {
            return fromKey;
        }

        public long getToKey() {
            return toKey;
        }

        public OrderStatsData getDelaySendStatsData() {
            return delaySendStatsData;
        }

        public void setDelaySendStatsData(OrderStatsData delaySendStatsData) {
            this.delaySendStatsData = delaySendStatsData;
        }

        public OrderStatsData getDelayAckStatsData() {
            return delayAckStatsData;
        }

        public void setDelayAckStatsData(OrderStatsData delayAckStatsData) {
            this.delayAckStatsData = delayAckStatsData;
        }

        public OrderStatsData getQpxSendStatsData() {
            return qpxSendStatsData;
        }

        public void setQpxSendStatsData(OrderStatsData qpxSendStatsData) {
            this.qpxSendStatsData = qpxSendStatsData;
        }

        public OrderStatsData getQpxAckStatsData() {
            return qpxAckStatsData;
        }

        public void setQpxAckStatsData(OrderStatsData qpxAckStatsData) {
            this.qpxAckStatsData = qpxAckStatsData;
        }

        public OrderStatsData getAccuStatsData() {
            return accuStatsData;
        }

        public void setAccuStatsData(OrderStatsData accuStatsData) {
            this.accuStatsData = accuStatsData;
        }

        public String getConsumerId() {
            return consumerId;
        }

        @Override
        public String toString() {
            return "QueryOrderParam [topicName=" + topicName + ", consumerId=" + consumerId + ", fromKey=" + fromKey
                    + ", toKey=" + toKey + ", delaySendStatsData=" + delaySendStatsData + ", delayAckStatsData="
                    + delayAckStatsData + ", qpxSendStatsData=" + qpxSendStatsData + ", qpxAckStatsData="
                    + qpxAckStatsData + ", accuStatsData=" + accuStatsData + "]";
        }

    }

}
