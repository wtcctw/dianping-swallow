package com.dianping.swallow.common.internal.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author mengwenchao
 *
 * 2015年5月31日 下午6:36:54
 */
public class GuavaCacheBuilder {

	public <K, V> LoadingCache<K, V> createGuavaCache(int count, CacheLoader<K, V> loader) {

		return CacheBuilder.newBuilder().maximumSize(count).build(loader);
	}

}
