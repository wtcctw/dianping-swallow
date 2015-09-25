package com.dianping.swallow.web.controller.filter;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:55:14
 */
public interface FilterChain<T, R> extends Filter<T, R>{

	FilterChain<T, R> addFilter(Filter<T, R> filter);
}
