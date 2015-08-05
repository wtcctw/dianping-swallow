package com.dianping.swallow.web.monitor.wapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerTopicStatisData;
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
	public List<ProducerServerStatsData> getServerStatsDatas(long timeKey) {
		Set<String> serverKeys = producerDataRetriever.getKeys(new CasKeys());
		if (serverKeys == null) {
			return null;
		}
		Iterator<String> iterator = serverKeys.iterator();
		List<ProducerServerStatsData> serverStatsDatas = new ArrayList<ProducerServerStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();

			if (StringUtils.equals(serverIp, TOTAL_KEY)) {
				continue;
			}

			ProducerServerStatisData serverStatisData = (ProducerServerStatisData) producerDataRetriever
					.getValue(new CasKeys(serverIp));
			if (serverStatisData == null) {
				continue;
			}
			NavigableMap<Long, Long> qpx = serverStatisData.getQpx(StatisType.SAVE);
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
			Long qpxValue = qpx.get(timeKey);
			if (qpxValue != null) {
				serverStatsData.setQps(qpxValue);
			}

			serverStatsDatas.add(serverStatsData);

		}
		return serverStatsDatas;
	}

	@Override
	public List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey) {
		Set<String> topicKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ProducerTopicStatsData> producerTopicStatsDatas = new ArrayList<ProducerTopicStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String topicName = String.valueOf(iterator.next());
			if (StringUtils.equals(topicName, TOTAL_KEY)) {
				continue;
			}
			ProducerTopicStatisData serverStatisData = (ProducerTopicStatisData) producerDataRetriever
					.getValue(new CasKeys(TOTAL_KEY, topicName));
			if (serverStatisData == null) {
				continue;
			}
			NavigableMap<Long, Long> topicQpxs = serverStatisData.getQpx(StatisType.SAVE);
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

			Long topicQpxValue = topicQpxs.get(timeKey);
			if (topicQpxValue != null) {
				producerTopicStatsData.setQps(topicQpxValue);
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
	public Set<String> getTopicIps(String topicName) {
		return producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
	}

}
