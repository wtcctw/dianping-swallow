package com.dianping.swallow.web.controller.filter;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.filter.config.ConfigureFilterChain;
import com.dianping.swallow.web.controller.filter.lion.LionFilterChain;
import com.dianping.swallow.web.controller.filter.validator.ValidatorFilterChain;


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

	public LionFilterChain createLionFilterChain() {
		return new LionFilterChain();
	}

	public ConfigureFilterChain createConfigureFilterChain() {
		return new ConfigureFilterChain();
	}
	
}
