package com.dianping.swallow.web.controller.filter;

import com.dianping.swallow.web.controller.filter.validator.ValidatorFilterChain;
import org.springframework.stereotype.Component;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午8:22:15
 */
@Component
public class FilterChainFactory {

	public ValidatorFilterChain createValidatorFilterChain() {
		return new ValidatorFilterChain();
	}

}
