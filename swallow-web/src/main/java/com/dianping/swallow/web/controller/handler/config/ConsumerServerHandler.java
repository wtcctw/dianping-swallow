package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.AbstractHandler;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mingdongli
 *         15/10/23 上午11:41
 */
@Component
public class ConsumerServerHandler extends AbstractHandler<TopicApplyDto,LionConfigureResult> implements Handler<TopicApplyDto,LionConfigureResult> {

    @Resource(name = "consumerServerResourceService")
    private ConsumerServerResourceService consumerServerResourceService;

    @Override
    public Object handle(TopicApplyDto value, LionConfigureResult result) {

        String groupName = value.getType().trim();
        Pair<String, ResponseStatus> pair = consumerServerResourceService.loadIdleConsumerServer(groupName);
        if(result == null){
            result = new LionConfigureResult();
        }
        result.setConsumerServer(pair.getFirst());
        return pair.getSecond();
    }

    public void setConsumerServerResourceService(ConsumerServerResourceService consumerServerResourceService) {
        this.consumerServerResourceService = consumerServerResourceService;
    }
}
