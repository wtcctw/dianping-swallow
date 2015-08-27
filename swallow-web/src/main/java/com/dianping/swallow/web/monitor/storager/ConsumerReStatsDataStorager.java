package com.dianping.swallow.web.monitor.storager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.dao.ConsumerIdReStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.model.ConsumerIdReStatsData;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午1:31:02
 */
@Component
public class ConsumerReStatsDataStorager extends AbstractReStatsDataStorager implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerIdReStatsDataDao consumerIdReStatsDataDao;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	protected void doStorage() {
		if (dataCount.get() <= 0) {
			return;
		}
		dataCount.decrementAndGet();
		Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap = consumerStatsDataWapper
				.getConsumerIdStatsDatas(lastTimeKey.get());
		storageConsumerIdReStatsDatas(consumerIdStatsDataMap);
	}

	private void storageConsumerIdReStatsDatas(final Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap) {
		logger.info("[storageConsumerIdReStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageConsumerIdReStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (consumerIdStatsDataMap == null) {
					return;
				}
				boolean isFirstTime = true;
				for (Map.Entry<String, List<ConsumerIdStatsData>> consumerIdStatsDataEntry : consumerIdStatsDataMap
						.entrySet()) {
					List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatsDataEntry.getValue();
					if (consumerIdStatsDatas == null || MonitorData.TOTAL_KEY.equals(consumerIdStatsDataEntry.getKey())) {
						continue;
					}
					for (ConsumerIdStatsData statsData : consumerIdStatsDatas) {
						if (MonitorData.TOTAL_KEY.equals(statsData.getConsumerId())) {
							continue;
						}
						if (isFirstTime) {
							lastTimeKey.set(statsData.getTimeKey());
							isFirstTime = false;
						}
						storageConsumerIdEntity(statsData);
					}
				}
			}
		});
	}

	public void storageConsumerIdEntity(ConsumerIdStatsData statsData) {
		String topicName = statsData.getTopicName();
		String consumerId = statsData.getConsumerId();
		List<ConsumerIdReStatsData> statsDatas = consumerIdReStatsDataDao.findByPage(topicName, consumerId, 1, 0);
		if (statsDatas == null || statsDatas.size() == 0) {
			consumerIdReStatsDataDao.insert(ConsumerIdReStatsData.createEntity(statsData));
		} else {
			ConsumerIdReStatsData reStatsData = statsDatas.get(0);
			if (isNextData(statsData.getTimeKey(), reStatsData.getFromTimeKey())) {
				consumerIdReStatsDataDao.insert(ConsumerIdReStatsData.createEntity(statsData));
			} else {
				consumerIdReStatsDataDao.update(ConsumerIdReStatsData.updateEntity(reStatsData, statsData));
			}
		}
	}
}
