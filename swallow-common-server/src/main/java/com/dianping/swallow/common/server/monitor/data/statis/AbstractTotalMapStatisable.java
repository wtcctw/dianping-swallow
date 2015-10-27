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

	protected Statisable<?> getValue(Object key) {
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

		Statisable<?> value = getValue(key);
		if (value == null) {
			return null;
		}
		return value.getDelay(type);
	}

	@Override
	public NavigableMap<Long, QpxData> getQpx(StatisType type, Object key) {

		Statisable<?> value = getValue(key);
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
	public Object getValue(CasKeys keys, StatisType type) {

		if (!keys.hasNextKey()) {
			throw new UnfoundKeyException(keys.toString());
		}

		String key = keys.getNextKey();
		Statisable<M> result = map.get(key);

		if (result == null) {
			throw new UnfoundKeyException("key:" + key);
		}

		if (keys.hasNextKey()) {

			if (result instanceof MapRetriever) {
				return ((MapRetriever) result).getValue(keys, type);
			} else {
				throw new IllegalArgumentException("has next key, but next is not Map type!!");
			}

		} else {
			return result;
		}
	}

	@Override
	public Set<String> getKeys(CasKeys keys) {

		return getKeys(keys, null);
	}

	public Object getValue(CasKeys keys) {

		return getValue(keys, null);

	}

	@Override
	public String toString() {

		return JsonBinder.getNonEmptyBinder().toPrettyJson(map);
	}

	public String toString(String key) {

		return JsonBinder.getNonEmptyBinder().toPrettyJson(map.get(key));
	}

//	@Override
//	public void merge(Mergeable merge){
//
//		checkType(merge);
//
//		AbstractTotalMapStatisable<M,V> toMerge = (AbstractTotalMapStatisable<M, V>) merge;
//
//		if(hasTotal(this) && hasTotal(toMerge)){
//
//			getValue(MonitorData.TOTAL_KEY).merge(toMerge.map.get(MonitorData.TOTAL_KEY));
//			return;
//		}
//
//		for(java.util.Map.Entry<String, Statisable<M>> entry : toMerge.map.entrySet()){
//
//			String key = entry.getKey();
//			Mergeable value = entry.getValue();
//
//			if(hasTotal(this)){
//				getValue(MonitorData.TOTAL_KEY).merge(value);
//				continue;
//			}
//			Statisable<M> myValue = getOrCreate(key);
//			myValue.merge(value);
//		}
//	}

	@Override
	public void merge(Mergeable merge){

		AbstractTotalMapStatisable<M,V> toMerge = (AbstractTotalMapStatisable<M, V>) merge;

		for(java.util.Map.Entry<String, Statisable<M>> entry : toMerge.map.entrySet()){

			String key = entry.getKey();
			Mergeable value = entry.getValue();

			Statisable<M> myValue = map.get(key);
			if(myValue == null){
				myValue= createValue();
				map.put(key, myValue);
			}
			myValue.merge(value);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException{

		AbstractTotalMapStatisable<M,V> newMap = (AbstractTotalMapStatisable<M,V>) super.clone();

		for(java.util.Map.Entry<String, Statisable<M>> entry : map.entrySet()){

			String key = entry.getKey();
			Statisable<M> value = entry.getValue();
			newMap.map.put(key, (Statisable<M>) value.clone());
		}

		return map;
	}

	@Override
	public void merge(String key, KeyMergeable merge){

		checkType(merge);

		AbstractTotalMapStatisable<M,V> toMerge = (AbstractTotalMapStatisable<M, V>) merge;
		Statisable<M> value = toMerge.map.get(key);
		if( value == null){
			logger.warn("[merge][value null]" + key + "," + merge );
			return;
		}

		Statisable<M> myValue = getOrCreate(key);
		if(myValue instanceof KeyMergeable && value instanceof KeyMergeable){

			((KeyMergeable)myValue).merge(key, (KeyMergeable)value);
		}else{

			myValue.merge(value);
		}
	}

	private void checkType(Object merge) {

		if(!(merge instanceof AbstractTotalMapStatisable)){
			throw new IllegalArgumentException("wrong type : " + merge.getClass());
		}
	}

	private boolean hasTotal(AbstractTotalMapStatisable merge){

		Set<String> keys = merge.map.keySet();
		if(keys != null && keys.contains(MonitorData.TOTAL_KEY)){
			return true;
		}
		return false;
	}

	private Statisable<M> getOrCreate(String key) {
		Statisable<M> m = map.get(key);
		if(m == null){
			m= createValue();
			map.put(key, m);
		}
		return m;
	}

	protected abstract Statisable<M> createValue();

}
