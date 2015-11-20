package com.dianping.swallow.web.dashboard.wrapper;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

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

	public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {

		NavigableMap<Long, Long> map = consumerDataRetriever.getDelayValue(keys, type);
		if(map == null){
			map = new ConcurrentSkipListMap<Long, Long>();
		}
		return map;
	}

	public NavigableMap<Long, Long> getQpsValue(CasKeys keys, StatisType type) {

		NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
		NavigableMap<Long, Statisable.QpxData> map = consumerDataRetriever.getQpsValue(keys, type);
		if(map == null){
			return result;
		}

		for(Map.Entry<Long, Statisable.QpxData> entry : map.entrySet()){
			Statisable.QpxData qpsData = entry.getValue();
			Long qps = 0L;
			if(qpsData != null){
				qps = qpsData.getQpx(QPX.SECOND);
			}
			result.put(entry.getKey(), qps);
		}
		return result;
	}

	public Set<String> getKeyWithoutTotal(String... keys) {

		Set<String> set = consumerDataRetriever.getKeys(new CasKeys(keys));
		if (logger.isDebugEnabled() && set != null && keys != null) {
			logger.debug(String.format("Load keys %s without total of %s", set.toString(), keys.toString()));
		}

		if (set != null) {
			removeTotal(set);
			return set;
		} else {
			return Collections.emptySet();
		}

	}

	public void registerListener(MonitorDataListener monitorDataListener) {

		consumerDataRetriever.registerListener(monitorDataListener);
	}

	private void removeTotal(Set<String> set) {

		if (set.contains(TOTAL)) {
			set.remove(TOTAL);
		}
	}

}
