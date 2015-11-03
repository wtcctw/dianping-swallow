package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mengwenchao
 *
 *         2015年5月20日 下午2:09:12
 */
public abstract class AbstractTotalMapStatisable<M extends Mergeable, V extends TotalMap<M>> extends
		AbstractStatisable<V> implements MapStatisable<V> {

	protected Map<String, Statisable<M>> map = new ConcurrentHashMap<String, Statisable<M>>();

	@JsonIgnore
	private ThreadLocal<AtomicInteger> step = new ThreadLocal<AtomicInteger>();

	public Set<String> keySet(boolean includeTotal) {

		Set<String> result = new HashSet<String>(map.keySet());
		if (!includeTotal && !isOnlyTotal()) {
			result.remove(MonitorData.TOTAL_KEY);
		}
		return result;
	}

	@Override
	public void add(Long time, V added) {

		for (Entry<String, M> entry : added.entrySet()) {

			String addKey = entry.getKey();
			M addValue = entry.getValue();

			Statisable<M> realValue = MapUtil.getOrCreate(map, addKey, getStatisClass());
			realValue.add(time, addValue);
		}
	}

	protected Statisable<M> getValue(Object key) {
		return map.get(key);
	}

	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {

		for (Entry<String, Statisable<M>> entry : map.entrySet()) {

			String key = entry.getKey();
			Statisable<M> value = entry.getValue();

			try {
				increateStep();
				if (logger.isDebugEnabled()) {
					logger.debug("[build]" + getStepDebug() + key);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("[build]" + value);
				}
				value.build(qpx, startKey, endKey, intervalCount);
			} finally {
				decreateStep();
			}

			if (logger.isDebugEnabled()) {
				if (value instanceof MessageInfoStatis) {
					logger.debug("[build]" + getStepDebug() + value);
				}
			}
		}

	}

	private void increateStep() {
		if (step.get() == null) {
			step.set(new AtomicInteger());
		}

		step.get().incrementAndGet();
	}

	private void decreateStep() {
		step.get().decrementAndGet();
	}

	protected String getStepDebug() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= step.get().get(); i++) {
			sb.append("-----:");
		}
		return sb.toString();
	}

	@Override
	public void cleanEmpty() {

		for (String key : map.keySet()) {

			AbstractStatisable<M> value = (AbstractStatisable<M>) map.get(key);
			value.cleanEmpty();

			if (value.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("[clean]" + key);
				}
				map.remove(key);
			}
		}
	}

	@Override
	public boolean isEmpty() {

		for (Statisable<M> value : map.values()) {
			if (!value.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void doRemoveBefore(Long time) {

		for (Statisable<M> value : map.values()) {
			value.removeBefore(time);
		}
	}

	protected abstract Class<? extends Statisable<M>> getStatisClass();

	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type, Object key) {

		Statisable<M> value = getValue(key);
		if (value == null) {
			return null;
		}
		return value.getDelay(type);
	}

	@Override
	public NavigableMap<Long, QpxData> getQpx(StatisType type, Object key) {

		Statisable<M> value = getValue(key);
		if (value == null) {
			return null;
		}
		return value.getQpx(type);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> allDelay(StatisType type, boolean includeTotal) {

		Map<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long, Long>>();

		for (Entry<String, Statisable<M>> entry : map.entrySet()) {

			String key = entry.getKey();
			Statisable<M> value = entry.getValue();
			if (!isOnlyTotal() && !includeTotal && isTotalKey(key)) {
				continue;
			}
			result.put(key, value.getDelay(type));
		}
		return result;
	}

	private boolean isOnlyTotal() {
		return map.size() == 1;
	}

	@Override
	public Map<String, NavigableMap<Long, QpxData>> allQpx(StatisType type, boolean includeTotal) {

		Map<String, NavigableMap<Long, QpxData>> result = new HashMap<String, NavigableMap<Long, QpxData>>();

		for (Entry<String, Statisable<M>> entry : map.entrySet()) {

			String key = entry.getKey();
			Statisable<M> value = entry.getValue();
			if (!isOnlyTotal() && !includeTotal && isTotalKey(key)) {
				continue;
			}

			result.put(key, value.getQpx(type));
		}
		return result;

	}

	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type) {

		return getDelay(type, MonitorData.TOTAL_KEY);
	}

	@Override
	public NavigableMap<Long, QpxData> getQpx(StatisType type) {

		return getQpx(type, MonitorData.TOTAL_KEY);
	}

	@Override
	public Set<String> getKeys(CasKeys keys, StatisType type) {

		if (!keys.hasNextKey()) {
			return new HashSet<String>(map.keySet());
		}

		String key = keys.getNextKey();
		Statisable<M> result = map.get(key);

		if (result == null) {
			return Collections.emptySet();
		}

		if (result instanceof MapRetriever) {
			return ((MapRetriever) result).getKeys(keys, type);
		}

		throw new IllegalArgumentException("next not instanceof Map type!" + result.getClass());
	}

	@Override
	public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {

		if (!keys.hasNextKey()) {
			return getDelay(type);
		}

		String key = keys.getNextKey();
		Statisable<M> result = map.get(key);

		if (result == null) {
			return new ConcurrentSkipListMap<Long, Long>();
		}

		if (keys.hasNextKey()) {

			if (result instanceof MapRetriever) {
				return ((MapRetriever) result).getDelayValue(keys, type);
			} else {
				throw new IllegalArgumentException("has next key, but next is not Map type!!");
			}

		} else {
			return result.getDelay(type);
		}
	}

	@Override
	public NavigableMap<Long, Statisable.QpxData> getQpsValue(CasKeys keys, StatisType type) {

		if (!keys.hasNextKey()) {
			return getQpx(type);
		}

		String key = keys.getNextKey();
		Statisable<M> result = map.get(key);

		if (result == null) {
			throw new UnfoundKeyException("key:" + key);
		}

		if (keys.hasNextKey()) {

			if (result instanceof MapRetriever) {
				return ((MapRetriever) result).getQpsValue(keys, type);
			} else {
				throw new IllegalArgumentException("has next key, but next is not Map type!!");
			}

		} else {
			return result.getQpx(type);
		}
	}

	@Override
	public Set<String> getKeys(CasKeys keys) {

		return getKeys(keys, null);
	}

//	@Override
//	public Statisable getValue(CasKeys keys) {
//
//		return getValue(keys, null);
//
//	}

	@Override
	public String toString() {

		return JsonBinder.getNonEmptyBinder().toPrettyJson(map);
	}

	public String toString(String key) {

		return JsonBinder.getNonEmptyBinder().toPrettyJson(map.get(key));
	}

}
