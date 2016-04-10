package com.dianping.swallow.web.monitor.wapper;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.model.stats.StatsDataFactory;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 下午3:23:48
 */
@Service("producerStatsDataWapper")
public class ProducerStatsDataWapperImpl extends AbstractStatsDataWapper implements ProducerStatsDataWapper {

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Autowired
    private StatsDataFactory statsDataFactory;

    @Override
    public List<ProducerServerStatsData> getServerStatsDatas(long timeKey, boolean isTotal) {
        Set<String> serverKeys = producerDataRetriever.getKeys(new CasKeys());
        if (serverKeys == null) {
            return null;
        }
        Iterator<String> iterator = serverKeys.iterator();
        List<ProducerServerStatsData> serverStatsDatas = new ArrayList<ProducerServerStatsData>();
        boolean isFirst = true;
        while (iterator.hasNext()) {
            String serverIp = iterator.next();
            if (!isTotal && TOTAL_KEY.equals(serverIp)) {
                continue;
            }
            NavigableMap<Long, StatisData> statisDatas = null;
            if (isFirst) {
                statisDatas = producerDataRetriever.getMaxData(new CasKeys(serverIp), StatisType.SAVE);
            } else {
                statisDatas = producerDataRetriever.getStatisData(new CasKeys(serverIp), StatisType.SAVE, timeKey, timeKey);
            }
            if (statisDatas == null || statisDatas.isEmpty()) {
                continue;
            }
            if (isFirst) {
                if (statisDatas == null || statisDatas.isEmpty()) {
                    continue;
                }
                Long tempKey = timeKey == DEFAULT_VALUE ? statisDatas.lastKey() : statisDatas.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                isFirst = false;
            }
            StatisData statisData = statisDatas.get(timeKey);
            if (statisData != null) {
                ProducerServerStatsData serverStatsData = statsDataFactory.createProducerServerStatsData();
                serverStatsData.setTimeKey(timeKey);
                serverStatsData.setIp(serverIp);
                serverStatsData.setDelay(statisData.getAvgDelay());
                serverStatsData.setQps(statisData.getQpx(DEFAULT_QPX_TYPE));
                serverStatsData.setQpsTotal(statisData.getCount());
                serverStatsDatas.add(serverStatsData);
            }

        }
        return serverStatsDatas;
    }

    @Override
    public List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        Iterator<String> iterator = topicKeys.iterator();
        List<ProducerTopicStatsData> producerTopicStatsDatas = new ArrayList<ProducerTopicStatsData>();
        boolean isFirst = true;
        while (iterator.hasNext()) {
            String topicName = String.valueOf(iterator.next());
            if (!isTotal && TOTAL_KEY.equals(topicName)) {
                continue;
            }
            NavigableMap<Long, StatisData> statisDatas = null;
            if (isFirst) {
                statisDatas = producerDataRetriever.getMaxData(new CasKeys(TOTAL_KEY, topicName), StatisType.SAVE);
            } else {
                statisDatas = producerDataRetriever.getStatisData(new CasKeys(TOTAL_KEY, topicName), StatisType.SAVE, timeKey, timeKey);
            }
            if (statisDatas == null || statisDatas.isEmpty()) {
                continue;
            }
            if (isFirst) {
                if (statisDatas == null || statisDatas.isEmpty()) {
                    continue;
                }
                Long tempKey = timeKey == DEFAULT_VALUE ? statisDatas.lastKey() : statisDatas.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                isFirst = false;
            }
            StatisData statisData = statisDatas.get(timeKey);
            if (statisData != null) {
                ProducerTopicStatsData producerTopicStatsData = statsDataFactory.createTopicStatsData();
                producerTopicStatsData.setTopicName(topicName);
                producerTopicStatsData.setTimeKey(timeKey);
                producerTopicStatsData.setQps(statisData.getQpx(DEFAULT_QPX_TYPE));
                producerTopicStatsData.setQpsTotal(statisData.getCount());
                producerTopicStatsData.setDelay(statisData.getAvgDelay());
                producerTopicStatsData.setMsgSize(statisData.getAvgMsgSize());
                producerTopicStatsDatas.add(producerTopicStatsData);
            }
        }
        return producerTopicStatsDatas;
    }

    @Override
    public List<ProducerIpStatsData> getIpStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        List<ProducerIpStatsData> ipStatsDatas = new ArrayList<ProducerIpStatsData>();
        for (String topic : topicKeys) {
            if (!isTotal && TOTAL_KEY.equals(topic)) {
                continue;
            }
            List<ProducerIpStatsData> tempIpStatsDatas = getIpStatsDatas(topic, timeKey, isTotal);
            if (tempIpStatsDatas != null) {
                ipStatsDatas.addAll(tempIpStatsDatas);
            }
        }
        return ipStatsDatas;
    }

    @Override
    public List<ProducerIpStatsData> getIpStatsDatas(String topicName, long timeKey, boolean isTotal) {
        List<ProducerIpStatsData> ipStatsDatas = new ArrayList<ProducerIpStatsData>();
        Set<String> ipKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
        if (ipKeys == null) {
            return null;
        }
        boolean isFirst = true;
        for (String ip : ipKeys) {
            if (!isTotal && TOTAL_KEY.equals(ip)) {
                continue;
            }
            NavigableMap<Long, StatisData> statisDatas = null;
            if (isFirst) {
                statisDatas = producerDataRetriever.getMaxData(new CasKeys(TOTAL_KEY, topicName, ip), StatisType.SAVE);
            } else {
                statisDatas = producerDataRetriever.getStatisData(new CasKeys(TOTAL_KEY, topicName, ip), StatisType.SAVE, timeKey, timeKey);
            }
            if (statisDatas == null || statisDatas.isEmpty()) {
                continue;
            }
            if (isFirst) {
                if (statisDatas == null || statisDatas.isEmpty()) {
                    continue;
                }
                Long tempKey = timeKey == DEFAULT_VALUE ? statisDatas.lastKey() : statisDatas.higherKey(timeKey);
                if (tempKey == null) {
                    return null;
                }
                timeKey = tempKey.longValue();
                isFirst = false;
            }
            StatisData statisData = statisDatas.get(timeKey);
            if (statisData != null) {
                ProducerIpStatsData ipStatsData = statsDataFactory.createProducerIpStatsData();
                ipStatsData.setTopicName(topicName);
                ipStatsData.setTimeKey(timeKey);
                ipStatsData.setIp(ip);
                ipStatsData.setQps(statisData.getQpx(DEFAULT_QPX_TYPE));
                ipStatsData.setQpsTotal(statisData.getCount());
                ipStatsData.setDelay(statisData.getAvgDelay());
                ipStatsDatas.add(ipStatsData);
            }
        }

        return ipStatsDatas;
    }

    @Override
    public List<ProducerIpGroupStatsData> getIpGroupStatsDatas(long timeKey, boolean isTotal) {
        Set<String> topicKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (topicKeys == null) {
            return null;
        }
        List<ProducerIpGroupStatsData> ipGroupStatsDatas = new ArrayList<ProducerIpGroupStatsData>();
        for (String topic : topicKeys) {
            if (!isTotal && TOTAL_KEY.equals(topic)) {
                continue;
            }
            ipGroupStatsDatas.add(getIpGroupStatsData(topic, timeKey, isTotal));
        }
        return ipGroupStatsDatas;
    }

    @Override
    public ProducerIpGroupStatsData getIpGroupStatsData(String topicName, long timeKey, boolean isTotal) {
        ProducerIpGroupStatsData ipGroupStatsData = new ProducerIpGroupStatsData();
        List<ProducerIpStatsData> ipStatsDatas = getIpStatsDatas(topicName, timeKey, isTotal);
        ipGroupStatsData.setIpStatsDatas(ipStatsDatas);
        return ipGroupStatsData;
    }

    @Override
    public Set<String> getTopicIps(String topicName, boolean isTotal) {
        Set<String> topicIps = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
        if (!isTotal && topicIps != null) {
            if (topicIps.contains(TOTAL_KEY)) {
                topicIps.remove(TOTAL_KEY);
            }
        }
        return topicIps;
    }

    @Override
    public Set<String> getTopics(boolean isTotal) {
        Set<String> topics = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
        if (!isTotal && topics != null) {
            if (topics.contains(TOTAL_KEY)) {
                topics.remove(TOTAL_KEY);
            }
        }
        return topics;
    }

    @Override
    public Set<String> getServerIps(boolean isTotal) {
        Set<String> serverIps = producerDataRetriever.getKeys(new CasKeys());
        if (!isTotal && serverIps != null) {
            if (serverIps.contains(TOTAL_KEY)) {
                serverIps.remove(TOTAL_KEY);
            }
        }
        return serverIps;
    }

    public Set<String> getIps(boolean isTotal) {
        Set<String> ips = new HashSet<String>();
        Set<String> topics = getTopics(isTotal);
        if (topics != null) {
            for (String topic : topics) {
                Set<String> topicIps = getTopicIps(topic, isTotal);
                if (topicIps != null) {
                    ips.addAll(topicIps);
                }
            }
        }
        Set<String> serverIps = getServerIps(isTotal);
        if (serverIps != null) {
            ips.addAll(serverIps);
        }
        return ips;
    }

}
