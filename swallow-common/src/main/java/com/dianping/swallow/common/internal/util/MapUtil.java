package com.dianping.swallow.common.internal.util;

import java.util.Map;

import com.dianping.swallow.common.internal.pool.ObjectFactory;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:22:21
 */
public class MapUtil {

	
	public static <K, V> V getOrCreate(Map<K, V> map, K key, ObjectFactory<? extends V>  objectFactory){

		V ret  = map.get(key);
		
		if(ret != null){
			return ret;
		}

		synchronized (map) {
			ret = (V) map.get(key);
			if(ret == null){
				try {
					ret = objectFactory.createObject();
				} catch (Exception e){
					throw new IllegalStateException("error create object from factory:" + objectFactory, e);
				}
				map.put(key, ret);
			}
		}
		return ret;

	}

	public static <K, V, T extends V> V getOrCreate(Map<K, V> map, K key, Class<T> clazz){

		return getOrCreate(map, key, new ReflectObjectFactory<T>(clazz));
	}
	
	

	public static class ReflectObjectFactory<T> implements ObjectFactory<T>{
		
		private Class<T> clazz;

		public ReflectObjectFactory(Class<T> clazz){
			this.clazz = clazz;
		}
		
		@Override
		public T createObject() {
			try {
				return clazz.newInstance();
			} catch (Exception e){
				throw new IllegalStateException("error create object for " + clazz, e);
			}
		}

		@Override
		public Class<T> getObjectClass() {
			return clazz;
		}
		
		@Override
		public String toString() {
			return "object class:" + clazz;
		}
		
	} 

}
