package com.dianping.swallow.web.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年9月25日 下午2:13:47
 */
@Component
public class CaculateServerQpsTask extends AbstractTask {

	private static final long STATS_TIMEUNIT = 24 * 60 * 60;

	@Autowired
	private ConsumerServerResourceService cServerResourceService;

	@Autowired
	private ConsumerServerStatsDataService cServerStatsDataService;

	@Scheduled(cron = "0 5 0 ? * *")
	public void findQpsTask() {
		if (!isOpened()) {
			return;
		}
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-findQpsTask");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				logger.info("[findQpsTask] executor findConsumerQps.");
				findConsumerQps();
			}
		});
	}

	private void findConsumerQps() {
		List<ConsumerServerResource> consumerServerResources = cServerResourceService.findAll();

		for (ConsumerServerResource consumerServerResource : consumerServerResources) {

			if (consumerServerResource.getType() == ServerType.MASTER) {

				long startKey = AbstractRetriever.getKey(DateUtil.getStartPreNDays(0));
				long endKey = AbstractRetriever.getKey(DateUtil.getEndPreNDays(0));

				long qps = cServerStatsDataService.findQpsByServerIp(consumerServerResource.getIp(), startKey, endKey);

				ConsumerServerResource cServerResource = (ConsumerServerResource) cServerResourceService
						.findByIp(consumerServerResource.getIp());

				qps = qps * STATS_TIMEUNIT;

				if (cServerResource != null) {
					cServerResource.setQps(qps);
					cServerResource.setUpdateTime(new Date());
					cServerResourceService.update(cServerResource);
				}
			}
		}
	}
}
