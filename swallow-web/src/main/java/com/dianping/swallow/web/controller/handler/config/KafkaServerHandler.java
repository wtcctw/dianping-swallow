package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.AbstractHandler;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author   mingdongli
 * 16/3/1  上午10:58.
 */
@Component
public class KafkaServerHandler extends AbstractHandler<TopicApplyDto,LionConfigureResult> implements Handler<TopicApplyDto,LionConfigureResult> {

    private static final String PRE_KAFKA = "kafka://";

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @Override
    public Object handle(TopicApplyDto value, LionConfigureResult result) {
        String type = value.getType().trim();
        String zkChosen = null;
        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findByGroupName(type);
        if(kafkaServerResources != null && !kafkaServerResources.isEmpty()){
            for(KafkaServerResource kafkaServerResource : kafkaServerResources){
                if(kafkaServerResource.isActive()){
                    String zkServer = kafkaServerResource.getZkServers();
                    if(StringUtils.isNotBlank(zkServer)){
                        zkChosen = zkServer;
                        break;
                    }
                }
            }
        }
        if(StringUtils.isBlank(zkChosen)){
            return ResponseStatus.NOTEXIST;
        }
        if(result == null){
            result = new LionConfigureResult();
        }
        result.setStorageServer(PRE_KAFKA + zkChosen);
        result.setTopicType(value.buildKafkaTopicType());
        return ResponseStatus.SUCCESS;
    }

    public void setKafkaServerResourceService(KafkaServerResourceService kafkaServerResourceService) {
        this.kafkaServerResourceService = kafkaServerResourceService;
    }
}
