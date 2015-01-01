package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.AbstractHandler;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.MongoResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mingdongli
 *         15/10/23 下午2:31
 */
@Component
public class MongoServerHandler extends AbstractHandler<TopicApplyDto,LionConfigureResult> implements Handler<TopicApplyDto,LionConfigureResult> {

    @Resource(name = "mongoResourceService")
    private MongoResourceService mongoResourceService;

    @Override
    public Object handle(TopicApplyDto value, LionConfigureResult result) {

        String type = value.getType();
        String groupName = MongoType.findString(type);
        MongoResource mongoResource = mongoResourceService.findIdleMongoByType(groupName);
        if(mongoResource != null){
            String mongoChosen = mongoResource.getIp();
            if(StringUtils.isNotBlank(mongoChosen)){
                if(result == null){
                    result = new LionConfigureResult();
                }
                result.setMongoServer(mongoChosen);
                return ResponseStatus.SUCCESS;
            }
        }
        return ResponseStatus.NOTEXIST;

    }

    public void setMongoResourceService(MongoResourceService mongoResourceService) {
        this.mongoResourceService = mongoResourceService;
    }
}
