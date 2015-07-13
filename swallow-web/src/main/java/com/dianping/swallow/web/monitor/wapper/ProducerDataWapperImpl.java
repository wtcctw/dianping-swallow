package com.dianping.swallow.web.monitor.wapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerTopicStatisData;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;

/**
 *
 * @author qiyin
 *
 */
@Service("producerDataWapper")
public class ProducerDataWapperImpl extends AbstractDataWapper implements ProducerDataWapper {

	private static final Logger logger = LoggerFactory.getLogger(ProducerDataWapperImpl.class);

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Override
	public ProducerServerStatsData getServerStatsData(long timeKey) {
		Set<String> serverKeys = producerDataRetriever.getKeys(new CasKeys());
		if (serverKeys == null) {
			return null;
		}
		Iterator<String> iterator = serverKeys.iterator();
		ProducerServerStatsData serverStatsDataTemp = new ProducerServerStatsData();
		serverStatsDataTemp.setTimeKey(timeKey);
		List<ProducerMachineStatsData> machineStatsDatas = new ArrayList<ProducerMachineStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();
			if (StringUtils.equals(serverIp, TOTAL_KEY)) {
				continue;
			}
			logger.info("#######" + serverIp);
			ProducerServerStatisData serverStatisData = (ProducerServerStatisData) producerDataRetriever
					.getValue(new CasKeys(serverIp));
			logger.info("#######" + serverStatisData);
			NavigableMap<Long, Long> qpx = serverStatisData.getQpx(StatisType.SAVE);
			logger.info("#######" + String.valueOf(timeKey));
			if (qpx == null) {
				continue;
			}
			if (index == 0) {
				timeKey = timeKey == DEFAULT_VALUE ? qpx.lastKey() : qpx.higherKey(timeKey);
				index++;
			}
			serverStatsDataTemp.setTimeKey(timeKey);
			ProducerMachineStatsData machineStatsData = new ProducerMachineStatsData();
			machineStatsData.setIp(serverIp);
			ProducerBaseStatsData baseStatsData = new ProducerBaseStatsData();
			baseStatsData.setDelay(0);
			baseStatsData.setQpx(qpx.get(timeKey));
			machineStatsData.setStatisData(baseStatsData);
			machineStatsDatas.add(machineStatsData);

		}
		serverStatsDataTemp.setStatisDatas(machineStatsDatas);
		return serverStatsDataTemp;
	}

	@Override
	public List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey) {
		Set<String> topicKeys = producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ProducerTopicStatsData> producerTopicStatisDataTemps = new ArrayList<ProducerTopicStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			ProducerTopicStatsData producerTopicStatisData = new ProducerTopicStatsData();
			String topicName = String.valueOf(iterator.next());
			producerTopicStatisData.setTopicName(topicName);
			ProducerTopicStatisData serverStatisData = (ProducerTopicStatisData) producerDataRetriever
					.getValue(new CasKeys(TOTAL_KEY, topicName));
			NavigableMap<Long, Long> topicQpxs = serverStatisData.getQpx(StatisType.SAVE);
			if (index == 0) {
				timeKey = timeKey == DEFAULT_VALUE ? topicQpxs.lastKey() : topicQpxs.higherKey(timeKey);
				index++;
			}
			producerTopicStatisData.setTimeKey(timeKey);
			NavigableMap<Long, Long> topicDelays = serverStatisData.getDelay(StatisType.SAVE);
			ProducerBaseStatsData producerBaseStatisData = new ProducerBaseStatsData();
			producerBaseStatisData.setQpx(topicQpxs.get(timeKey));
			producerBaseStatisData.setDelay(topicDelays.get(timeKey));
			producerTopicStatisData.setProducerStatisData(producerBaseStatisData);
			producerTopicStatisDataTemps.add(producerTopicStatisData);
		}
		return producerTopicStatisDataTemps;
	}

	public Set<String> getTopicIps(String topicName) {
		return producerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
	}
}
