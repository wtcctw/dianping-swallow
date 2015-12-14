package com.dianping.swallow.web.controller.filter.validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String INVALID_TYPE = "mongo类型不存在，请选择正确的mongo类型，如有问题请联系mingdong.li@dianping.com";

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result,
			FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {

		String type = topicApplyDto.getType();
		if (StringUtils.isBlank(type)) {
			if (logger.isInfoEnabled()) {
				logger.info(INVALID_TYPE);
			}
			result.setMessage(INVALID_TYPE);
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
				logger.info(INVALID_TYPE);
			}
			result.setMessage(INVALID_TYPE);
			result.setStatus(-20);
			return;
		}
	}

}
