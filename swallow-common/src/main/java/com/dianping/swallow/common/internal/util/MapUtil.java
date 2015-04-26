package com.dianping.swallow.common.internal.util;

import java.util.Map;

import com.dianping.swallow.common.internal.monitor.Mergeable;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:22:21
 */
public class MapUtil {
	
	public static <K, V> V getOrCreate(Map<K, V> map, K key, Class<V> clazz){
		
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

}
