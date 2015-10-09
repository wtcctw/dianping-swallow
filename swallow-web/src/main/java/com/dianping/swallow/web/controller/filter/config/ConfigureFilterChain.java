package com.dianping.swallow.web.controller.filter.config;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.AbstractFilterChain;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:40:01
 */
@Component
public class ConfigureFilterChain extends AbstractFilterChain<TopicApplyDto, ConfigureFilterResult> implements FilterChain<TopicApplyDto, ConfigureFilterResult> {
	
	@Override
	public void doFilter(TopicApplyDto value, ConfigureFilterResult result, FilterChain<TopicApplyDto, ConfigureFilterResult> validatorChain) {
		
		if (index >= validators.size()){
			return;
		}
		Filter<TopicApplyDto, ConfigureFilterResult> v = validators.get(index);
		index++;
		v.doFilter(value, result, validatorChain);
	}

	@Override
	public FilterChain<TopicApplyDto, ConfigureFilterResult> addFilter(Filter<TopicApplyDto, ConfigureFilterResult> filter) {
		
		validators.add(filter);
		return this;
	}

}