package com.dianping.swallow.web.manager.impl;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.manager.CacheManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author mengwenchao
 *
 * 2014年12月4日 下午2:27:07
 */
@Component
public class DefaultCacheManager implements CacheManager{

	@Override
	public <K, V> LoadingCache<K, V> createGuavaCache(int count, 
			CacheLoader<K, V> loader) {
		
		return CacheBuilder.newBuilder().maximumSize(count).build(loader);
	}

}
