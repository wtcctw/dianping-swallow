package com.dianping.swallow.web.monitor.wapper;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.dashboard.DashboardContainerUpdater;

/**
 * @author mingdongli
 *
 *         2015年7月10日上午9:04:54
 */
@Component
public class ConsumerDataRetrieverWrapper {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	public static final String TOTAL = "total";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public Set<String> getKey(String... keys) {

		return consumerDataRetriever.getKeys(new CasKeys(keys));
	}

	public ConsumerIdStatisData getValue(String... keys) {

		ConsumerIdStatisData consumerIdStatisData = (ConsumerIdStatisData) consumerDataRetriever.getValue(new CasKeys(
				keys));
		if(consumerIdStatisData == null){
			consumerIdStatisData = new ConsumerIdStatisData();
		}
		return consumerIdStatisData;
	}

	public Set<String> getKeyWithoutTotal(String... keys) {

		Set<String> set = consumerDataRetriever.getKeys(new CasKeys(keys));
		if (logger.isInfoEnabled() && set != null && keys != null) {
			logger.info(String.format("Load keys %s without total of %s", set.toString(), keys.toString()));
		}

		if (set != null) {
			removeTotal(set);
			return set;
		} else {
			return Collections.emptySet();
		}

	}

	public void registerListener(DashboardContainerUpdater dashboardContainerUpdater) {

		consumerDataRetriever.registerListener(dashboardContainerUpdater);
	}

	private void removeTotal(Set<String> set) {

		if (set.contains(TOTAL)) {
			set.remove(TOTAL);
		}
	}

}
