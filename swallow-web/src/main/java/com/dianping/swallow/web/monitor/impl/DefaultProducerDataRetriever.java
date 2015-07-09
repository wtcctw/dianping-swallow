package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.dao.ProducerMonitorDao;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午11:04:09
 */
@Component
public class DefaultProducerDataRetriever
		extends
		AbstractMonitorDataRetriever<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData>
		implements ProducerDataRetriever {

	public static final String CAT_TYPE = "DefaultProducerDataRetriever";

	@Autowired
	private ProducerMonitorDao producerMonitorDao;

	@Override
	public StatsData getSaveDelay(String topic, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getDelayInMemory(topic, StatisType.SAVE, start, end);
		}

		return getDelayInDb(topic, StatisType.SAVE, start, end);
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getQpxInMemory(topic, StatisType.SAVE, start, end);
		}
		return getQpxInDb(topic, StatisType.SAVE, start, end);
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getServerQpxInMemory(qpx, StatisType.SAVE, start, end);
		}

		return getServerQpxInDb(qpx, StatisType.SAVE, start, end);
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx) {

		return getQpx(topic, qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public StatsData getSaveDelay(final String topic) throws Exception {

		SwallowCallableWrapper<StatsData> wrapper = new CatCallableWrapper<StatsData>(CAT_TYPE, "getSaveDelay");

		return wrapper.doCallable(new Callable<StatsData>() {

			@Override
			public StatsData call() throws Exception {

				return getSaveDelay(topic, getDefaultStart(), getDefaultEnd());
			}
		});
	}

	@Override
	protected AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> createServerStatis() {

		return new ProducerAllData();
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx) {
		return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected StatsDataDesc createDelayDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getDelayDetailType());
	}

	@Override
	protected StatsDataDesc createQpxDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
	}

	@Override
	public ProducerServerStatsData getServerStatis(long timeKey, StatisType type) {
		Map<String, NavigableMap<Long, Long>> qpxForServers = statis.getQpxForServers(type);
		ProducerServerStatsData serverStatsDataTemp = new ProducerServerStatsData();
		List<ProducerMachineStatsData> machineStatsDatas = new ArrayList<ProducerMachineStatsData>();
		if (qpxForServers == null) {
			return null;
		}
		for (Map.Entry<String, NavigableMap<Long, Long>> stats : qpxForServers.entrySet()) {
			String serverIp = stats.getKey();
			timeKey = timeKey == -1 ? stats.getValue().lastKey() : stats.getValue().higherKey(timeKey);
			serverStatsDataTemp.setTimeKey(timeKey);
			ProducerMachineStatsData machineStatsData = new ProducerMachineStatsData();
			machineStatsData.setIp(serverIp);
			ProducerBaseStatsData baseStatsData = new ProducerBaseStatsData();
			baseStatsData.setDelay(0);
			baseStatsData.setQpx(timeKey);
			machineStatsData.setStatisData(baseStatsData);
			machineStatsDatas.add(machineStatsData);
		}
		serverStatsDataTemp.setStatisDatas(machineStatsDatas);
		return serverStatsDataTemp;
	}

	@Override
	public List<ProducerTopicStatsData> getTopicStatis(long timeKey, StatisType type) {
		Set<String> topics = statis.getTopics(true);
		if (topics == null) {
			return null;
		}
		List<ProducerTopicStatsData> producerTopicStatisDataTemps = new ArrayList<ProducerTopicStatsData>();
		Iterator<String> iterator = topics.iterator();
		while (iterator.hasNext()) {
			ProducerTopicStatsData producerTopicStatisData = new ProducerTopicStatsData();
			String topicName = String.valueOf(iterator.next());
			producerTopicStatisData.setTopicName(topicName);
			NavigableMap<Long, Long> topicQpxs = statis.getQpxForTopic(topicName, type);
			timeKey = timeKey == -1 ? timeKey = topicQpxs.lastKey() : topicQpxs.higherKey(timeKey);
			producerTopicStatisData.setTimeKey(timeKey);
			NavigableMap<Long, Long> topicDelays = statis.getDelayForTopic(topicName, type);
			ProducerBaseStatsData producerBaseStatisData = new ProducerBaseStatsData();
			producerBaseStatisData.setQpx(topicQpxs.get(timeKey));
			producerBaseStatisData.setDelay(topicDelays.get(timeKey));
			producerTopicStatisData.setProducerStatisData(producerBaseStatisData);
			producerTopicStatisDataTemps.add(producerTopicStatisData);
		}
		return producerTopicStatisDataTemps;
	}
}