package com.dianping.swallow.web.monitor.wapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.MessageInfoStatis;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerTopicStatisData;
import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.model.stats.StatsDataFactory;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;

/**
 * 
 * @author qiyin
 *
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
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(serverIp)) {
				continue;
			}
			ProducerServerStatisData serverStatisData = (ProducerServerStatisData) producerDataRetriever
					.getValue(new CasKeys(serverIp));
			if (serverStatisData == null) {
				continue;
			}
			NavigableMap<Long, QpxData> qpx = serverStatisData.getQpx(StatisType.SAVE);
			if (qpx == null || qpx.isEmpty()) {
				continue;
			}

			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? qpx.lastKey() : qpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}

			ProducerServerStatsData serverStatsData = statsDataFactory.createProducerServerStatsData();
			serverStatsData.setTimeKey(timeKey);
			serverStatsData.setIp(serverIp);
			serverStatsData.setDelay(0);
			QpxData qpxValue = qpx.get(timeKey);
			if (qpxValue != null) {
				serverStatsData.setQps(qpxValue.getQpx() == null ? 0L : qpxValue.getQpx().longValue());
				serverStatsData.setQpsTotal(qpxValue.getTotal() == null ? 0L : qpxValue.getTotal().longValue());
			}

			serverStatsDatas.add(serverStatsData);

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
		int index = 0;
		while (iterator.hasNext()) {
			String topicName = String.valueOf(iterator.next());
			if (!isTotal && TOTAL_KEY.equals(topicName)) {
				continue;
			}
			ProducerTopicStatisData serverStatisData = (ProducerTopicStatisData) producerDataRetriever
					.getValue(new CasKeys(TOTAL_KEY, topicName));
			if (serverStatisData == null) {
				continue;
			}
			NavigableMap<Long, QpxData> topicQpxs = serverStatisData.getQpx(StatisType.SAVE);
			if (topicQpxs == null || topicQpxs.isEmpty()) {
				continue;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? topicQpxs.lastKey() : topicQpxs.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}
			ProducerTopicStatsData producerTopicStatsData = statsDataFactory.createTopicStatsData();
			producerTopicStatsData.setTopicName(topicName);
			producerTopicStatsData.setTimeKey(timeKey);
			NavigableMap<Long, Long> topicDelays = serverStatisData.getDelay(StatisType.SAVE);

			QpxData topicQpx = topicQpxs.get(timeKey);
			if (topicQpx != null) {
				producerTopicStatsData.setQps(topicQpx.getQpx() == null ? 0L : topicQpx.getQpx().longValue());
				producerTopicStatsData.setQpsTotal(topicQpx.getTotal() == null ? 0L : topicQpx.getTotal().longValue());
			}
			Long delay = topicDelays.get(timeKey);
			if (delay != null) {
				producerTopicStatsData.setDelay(delay.longValue());
			}
			producerTopicStatsDatas.add(producerTopicStatsData);
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
		int index = 0;
		for (String ip : ipKeys) {
			if (!isTotal && TOTAL_KEY.equals(ip)) {
				continue;
			}

			MessageInfoStatis messageStatisData = (MessageInfoStatis) producerDataRetriever.getValue(new CasKeys(
					TOTAL_KEY, topicName, ip));
			if (messageStatisData == null) {
				continue;
			}
			NavigableMap<Long, QpxData> ipQpxs = messageStatisData.getQpx(StatisType.SAVE);
			if (ipQpxs == null || ipQpxs.isEmpty()) {
				continue;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? ipQpxs.lastKey() : ipQpxs.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}

			ProducerIpStatsData ipStatsData = statsDataFactory.createProducerIpStatsData();
			ipStatsData.setTopicName(topicName);
			ipStatsData.setTimeKey(timeKey);
			ipStatsData.setIp(ip);
			NavigableMap<Long, Long> ipDelays = messageStatisData.getDelay(StatisType.SAVE);
			QpxData qps = ipQpxs.get(timeKey);
			if (qps != null) {
				ipStatsData.setQps(qps.getQpx() == null ? 0L : qps.getQpx().longValue());
				ipStatsData.setQpsTotal(qps.getTotal() == null ? 0L : qps.getTotal().longValue());
			}

			Long delay = ipDelays.get(timeKey);
			if (delay != null) {
				ipStatsData.setDelay(delay.longValue());
			}
			ipStatsDatas.add(ipStatsData);
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
		ipGroupStatsData.setProducerIpStatsDatas(ipStatsDatas);
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
