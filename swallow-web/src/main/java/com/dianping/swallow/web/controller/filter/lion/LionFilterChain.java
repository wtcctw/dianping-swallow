package com.dianping.swallow.web.controller.filter.lion;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.filter.AbstractFilterChain;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:40:01
 */
@Component
public class LionFilterChain extends AbstractFilterChain<LionFilterEntity, LionFilterResult> implements FilterChain<LionFilterEntity, LionFilterResult> {
	
	@Override
	public void doFilter(LionFilterEntity value, LionFilterResult result, FilterChain<LionFilterEntity, LionFilterResult> validatorChain) {
		
		if (index >= validators.size()){
			return;
		}
		Filter<LionFilterEntity, LionFilterResult> v = validators.get(index);
		index++;
		v.doFilter(value, result, validatorChain);
	}

	@Override
	public FilterChain<LionFilterEntity, LionFilterResult> addFilter(Filter<LionFilterEntity, LionFilterResult> filter) {
		
		validators.add(filter);
		return this;
	}

}