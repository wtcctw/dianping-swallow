package com.dianping.swallow.web.controller.filter.validator;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.AbstractFilterChain;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午10:56:18
 */
@Component
public class ValidatorFilterChain extends AbstractFilterChain<TopicApplyDto, ValidatorFilterResult> implements FilterChain<TopicApplyDto, ValidatorFilterResult> {
	
	@Override
	public void doFilter(TopicApplyDto value, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		if (index >= validators.size()){
			return;
		}
		Filter<TopicApplyDto, ValidatorFilterResult> v = validators.get(index);
		index++;
		v.doFilter(value, result, validatorChain);
	}

	@Override
	public FilterChain<TopicApplyDto, ValidatorFilterResult> addFilter(Filter<TopicApplyDto, ValidatorFilterResult> filter) {
		
		validators.add(filter);
		return this;
	}

}
