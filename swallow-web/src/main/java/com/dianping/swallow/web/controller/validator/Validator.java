package com.dianping.swallow.web.controller.validator;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午4:05:09
 */
public interface Validator {

	ResponseStatus ValidateTopicApplyDto(TopicApplyDto topicApplyDto);
}
