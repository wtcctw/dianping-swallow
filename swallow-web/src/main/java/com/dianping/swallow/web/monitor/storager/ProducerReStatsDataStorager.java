package com.dianping.swallow.web.monitor.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.dao.ProducerTopicReStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.model.ProducerTopicReStatsData;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午1:41:02
 */
@Component
public class ProducerReStatsDataStorager extends AbstractReStatsDataStorager implements MonitorDataListener {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerTopicReStatsDataDao topicReStatsDataDao;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
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
		List<ProducerTopicStatsData> topicStatisDatas = producerStatsDataWapper.getTopicStatsDatas(lastTimeKey.get());
		storageTopicReStatsDatas(topicStatisDatas);
	}

	private void storageTopicReStatsDatas(final List<ProducerTopicStatsData> topicStatisDatas) {
		logger.info("[storageProducerTopicReStatsDatas]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(),
				"storageProducerTopicReStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatisDatas == null) {
					return;
				}
				boolean isFirstTime = true;
				for (ProducerTopicStatsData topicStatsData : topicStatisDatas) {
					if (isFirstTime) {
						lastTimeKey.set(topicStatsData.getTimeKey());
						isFirstTime = false;
					}
					storageTopicEntity(topicStatsData);
				}

			}
		});
	}

	private void storageTopicEntity(ProducerTopicStatsData topicStatsData) {
		String topicName = topicStatsData.getTopicName();
		List<ProducerTopicReStatsData> statsDatas = topicReStatsDataDao.findByPage(topicName, 1, 0);
		if (statsDatas == null || statsDatas.size() == 0) {
			topicReStatsDataDao.insert(ProducerTopicReStatsData.createEntity(topicStatsData));
		} else {
			ProducerTopicReStatsData topicReStatsData = statsDatas.get(0);
			if (isNextData(topicStatsData.getTimeKey(), topicReStatsData.getFromTimeKey())) {
				topicReStatsDataDao.insert(ProducerTopicReStatsData.createEntity(topicStatsData));
			} else {
				topicReStatsDataDao.update(ProducerTopicReStatsData.updateEntity(topicReStatsData, topicStatsData));
			}
		}
	}
}
