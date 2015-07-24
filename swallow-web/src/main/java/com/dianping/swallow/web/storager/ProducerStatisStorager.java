package com.dianping.swallow.web.storager;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
import com.dianping.swallow.web.service.ProducerMachineStatisDataService;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;

/**
 *
 * @author qiyin
 *
 */
@Component
public class ProducerStatisStorager extends AbstractStatisStorager implements MonitorDataListener {

	protected volatile AtomicLong dataCount = new AtomicLong();

	protected static final int INIT_VALUE = 0;

	protected static final long DEFAULT_VALUE = -1L;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerMachineStatisDataService machineStatisDataService;

	@Autowired
	private ProducerTopicStatisDataService topicStatisDataService;

	private volatile List<ProducerTopicStatsData> topicStatisDatas;

	private volatile ProducerServerStatsData serverStatisData;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public void doStorage() {
		if (dataCount.get() > 0) {
			dataCount.incrementAndGet();
			serverStatisData = producerDataWapper.getServerStatsData(lastTimeKey.get());
			if (serverStatisData != null && serverStatisData.getTimeKey() != 0L) {
				lastTimeKey.set(serverStatisData.getTimeKey());
				topicStatisDatas = producerDataWapper.getTopicStatsDatas(lastTimeKey.get());
				storageServerStatis();
				storageTopicStatis();
			}
		}
	}

	private void storageServerStatis() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageServerStatis");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatisData != null) {
					for (ProducerMachineStatsData producerMachineStatsData : serverStatisData.getStatisDatas()) {
						machineStatisDataService.insert(producerMachineStatsData);
					}
				}
			}
		});
	}

	private void storageTopicStatis() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageTopicStatis");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatisDatas != null) {
					for (ProducerTopicStatsData producerTopicStatisData : topicStatisDatas)
						topicStatisDataService.insert(producerTopicStatisData);
				}
			}
		});
	}
}
