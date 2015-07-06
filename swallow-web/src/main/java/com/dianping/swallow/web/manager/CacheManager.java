package com.dianping.swallow.web.manager;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author mengwenchao
 *
 * 2014年12月4日 下午2:25:13
 */
public interface CacheManager {
	
	
	<K,V> LoadingCache<K, V> createGuavaCache(int count, CacheLoader<K,V> loader);

}
