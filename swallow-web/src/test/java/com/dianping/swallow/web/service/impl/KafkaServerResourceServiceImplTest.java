package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author   mingdongli
 * 16/2/17  下午4:09.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class KafkaServerResourceServiceImplTest {

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @Test
    public void test() {

        List<KafkaServerResource> kafkaServerResourceList =  kafkaServerResourceService.findAll();
        System.out.println("size is " + kafkaServerResourceList.size());
        Assert.assertTrue(kafkaServerResourceList.size() == 1);
    }

}