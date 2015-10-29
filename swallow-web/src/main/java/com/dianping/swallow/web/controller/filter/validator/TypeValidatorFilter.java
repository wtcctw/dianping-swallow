package com.dianping.swallow.web.controller.filter.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.model.resource.MongoType;

/**
 * @author mingdongli
 *
 *         2015年9月24日上午11:05:52
 */
@Component
public class TypeValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult> {

	protected final Logger logger = LogManager.getLogger(getClass());

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result,
			FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {

		String type = topicApplyDto.getType();
		if (StringUtils.isBlank(type)) {
			if (logger.isInfoEnabled()) {
				logger.info("Fail TypeFilter");
			}
			result.setMessage("invalid mongo type");
			result.setStatus(-20);
			return;
		}

		try {
			MongoType.findByType(type.trim());
			if (logger.isInfoEnabled()) {
				logger.info("Pass TypeFilter");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Fail TypeFilter");
			}
			result.setMessage("invalid mongo type");
			result.setStatus(-20);
			return;
		}
	}

}
