package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.model.resource.TopicApplyResource;
import com.dianping.swallow.web.service.TopicApplyService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Author   mingdongli
 * 15/11/20  下午12:30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class TopicApplyServiceImplTest {

    @Resource(name = "topicApplyService")
    private TopicApplyService topicApplyService;

    @Before
    public void setUp() throws Exception {

    }

    private TopicApplyResource createTopicApplyResource() {

        TopicApplyResource topicResource = new TopicApplyResource();

        TopicApplyDto topicApplyDto = new TopicApplyDto();

        LionConfigureResult lionConfigureResult = new LionConfigureResult();

        topicApplyDto.setAmount(1);
        topicApplyDto.setApprover("hongjun.zhong");
        topicApplyDto.setSize(1);
        topicApplyDto.setType("一般消息队列");
        topicApplyDto.setApplicant("yapu.wang");
        topicApplyDto.setTopic("topic_apply");

        lionConfigureResult.setConsumerServer("192.168.78.220:8081,192.168.79.100:8082");
        lionConfigureResult.setMongoServer("192.168.217.61:27017");
        lionConfigureResult.setSize4SevenDay(500);

        topicResource.setLionConfigureResult(lionConfigureResult);
        topicResource.setTopicApplyDto(topicApplyDto);
        topicResource.setResponseStatus(ResponseStatus.SUCCESS);

        topicResource.setCreateTime(new Date());
        topicResource.setTopic("topic_apply");

        return topicResource;
    }

    @Test
    public void test() {

        List<TopicApplyResource> topicResources = topicApplyService.find("topic_apply", 0, 30);
        if (topicResources == null) {
            TopicApplyResource topicResource = createTopicApplyResource();
            boolean result = topicApplyService.insert(topicResource);
            Assert.assertTrue(result);
        }else{
            topicResources.get(0).setCreateTime(new Date());
            boolean result = topicApplyService.insert(topicResources.get(0));
            Assert.assertTrue(result);
        }


    }
}