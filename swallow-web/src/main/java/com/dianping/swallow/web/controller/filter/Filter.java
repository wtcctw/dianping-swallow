package com.dianping.swallow.web.controller.filter;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:57:42
 */
public interface Filter<T, R> {
	
	void doFilter(final T value, final R result, FilterChain<T, R> chain);
}
