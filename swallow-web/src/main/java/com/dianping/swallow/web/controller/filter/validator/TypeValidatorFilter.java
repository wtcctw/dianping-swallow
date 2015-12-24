package com.dianping.swallow.web.controller.filter.validator;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.service.GroupResourceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mingdongli
 *         <p/>
 *         2015年9月24日上午11:05:52
 */
@Component
public class TypeValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String INVALID_TYPE = "mongo类型不存在，请选择正确的mongo类型，如有问题请联系mingdong.li@dianping.com";

    @Resource(name = "groupResourceService")
    private GroupResourceService groupResourceService;

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

        List<String> groupNames = groupResourceService.findAllGroupName();

        if (groupNames == null || groupNames.isEmpty() || !groupNames.contains(type.trim())) {
            result.setMessage("没有分组设置，请联系swallow开发者添加分组");
            result.setStatus(-20);
            return;
        }else{
            if (logger.isInfoEnabled()) {
                logger.info("Pass TypeFilter");
            }
            validatorChain.doFilter(topicApplyDto, result, validatorChain);
        }

    }

    public void setGroupResourceService(GroupResourceService groupResourceService) {
        this.groupResourceService = groupResourceService;
    }
}
