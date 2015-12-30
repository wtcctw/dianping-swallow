package com.dianping.swallow.web.controller.filter.validator;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Author   mingdongli
 * 15/11/20  上午10:26.
 */
@Component
public class SwitchValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult>, ConfigChangeListener {

    protected final Logger logger = LogManager.getLogger(getClass());

    private static final String SWALLOW_APPLY_TOPIC_SWITCH = "swallow.apply.topic.switch";

    @Autowired
    private DefaultDynamicConfig dynamicConfig;

    private String applyTopicSwitch;

    @PostConstruct
    public void initConsumerServerConfig() throws Exception {

        applyTopicSwitch = dynamicConfig.get(SWALLOW_APPLY_TOPIC_SWITCH);
        dynamicConfig.addConfigChangeListener(this);
    }

    @Override
    public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {

        if("true".equals(applyTopicSwitch)){
            if(logger.isInfoEnabled()){
                logger.info("Pass SwitchValidator");
            }
            validatorChain.doFilter(topicApplyDto, result, validatorChain);
        }else{
            if(logger.isInfoEnabled()){
                logger.info("Fail SwitchValidator");
            }
            result.setMessage("自动申请topic功能已关闭，请发送邮件到mingdong.li@dianping.com申请topic。");
            result.setStatus(-24);
            return;
        }
    }

    @Override
    public void onConfigChange(String key, String value) {
        if (key != null && key.equals(SWALLOW_APPLY_TOPIC_SWITCH)) {
            if (logger.isInfoEnabled()) {
                logger.info("[onChange][" + SWALLOW_APPLY_TOPIC_SWITCH + "]" + value);
            }
            this.applyTopicSwitch = value.trim();
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("not match");
            }
        }
    }

    public void setApplyTopicSwitch(String applyTopicSwitch) {
        this.applyTopicSwitch = applyTopicSwitch;
    }
}
