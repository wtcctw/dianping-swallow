package com.dianping.swallow.common.internal.util;

import com.dianping.swallow.common.internal.monitor.Mergeable;

import java.util.Map;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:22:21
 */
public class MapUtil {

	public static <K, V> V getOrCreate(Map<K, V> map, K key, Class<? extends V> clazz){

		V ret  = null;

		Object syn = key instanceof String ? ((String)key).intern() : key ;
		synchronized (syn) {
			ret = (V) map.get(key);
			if(ret == null){
				try {
					ret = clazz.newInstance();
				} catch (Exception e){
					throw new IllegalStateException("error create " + clazz, e);
				}
				map.put(key, ret);
			}
		}
		return ret;
	}

	public static <K, V extends Mergeable> void mergeMap(Map<K, V> toMerge, Map<K, V> fromMerge){
		if(toMerge == null || fromMerge == null){
			return;
		}
		for(Map.Entry<K, V> entry : fromMerge.entrySet()){
			K fromKey = entry.getKey();
			V fromValue = entry.getValue();
			V toValue = toMerge.get(fromKey);

			if(toValue == null){
				toMerge.put(fromKey, fromValue);
			}else{
				toValue.merge(fromValue);
				toMerge.put(fromKey, toValue);
			}
		}
	}

	public static void mergeMapOfTypeLong(Map<Long, Long> toMerge, Map<Long, Long> fromMerge){
		if(toMerge == null || fromMerge == null){
			return;
		}
		for(Map.Entry<Long, Long> entry : fromMerge.entrySet()){
			Long fromKey = entry.getKey();
			Long fromValue = entry.getValue();
			Long toValue = toMerge.get(fromKey);

			if(toValue == null){
				toMerge.put(fromKey, fromValue);
			}else{
				toValue += fromValue;
				toMerge.put(fromKey, toValue);
			}
		}
	}

}
