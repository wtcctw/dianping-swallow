package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.GroupConfig;
import com.dianping.swallow.common.internal.config.SwallowClientConfig;
import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.message.JsonDeserializedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qi.yin
 *         2016/01/26  下午3:17.
 */
public class SwallowClientConfigImplLocalTest extends AbstractTest {

    private SwallowClientConfig swallowClientConfig;

    @Before
    public void beforeTest() throws Exception {
        System.setProperty("SWALLOW.STORE.LION.CONFFILE", "swallow-store-lion-2.properties");
        swallowClientConfig = new SwallowClientConfigImpl();
    }

    @Override
    protected boolean useLocal() {
        return true;
    }

    @Test
    public void testDevTopicConfig() {
        TopicConfig topicConfig1 = swallowClientConfig.getTopicConfig("topic1");
        Assert.assertTrue(topicConfig1.getGroup() == null);
        Assert.assertTrue(topicConfig1.getSize() == 100);
        Assert.assertTrue(topicConfig1.getMax() == 100);
        Assert.assertTrue(topicConfig1.getStoreUrl() == null);
        Assert.assertTrue(topicConfig1.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig2 = swallowClientConfig.getTopicConfig("topic2");
        Assert.assertTrue(topicConfig2.getGroup() == null);
        Assert.assertTrue(topicConfig2.getSize() == null);
        Assert.assertTrue(topicConfig2.getMax() == null);
        Assert.assertTrue(topicConfig2.getStoreUrl() == null);
        Assert.assertTrue(topicConfig2.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig3 = swallowClientConfig.getTopicConfig("topic3");
        Assert.assertTrue(topicConfig3.getGroup() == null);
        Assert.assertTrue(topicConfig3.getSize() == 101);
        Assert.assertTrue(topicConfig3.getMax() == 102);
        Assert.assertTrue(topicConfig3.getStoreUrl().equals("mongodb://192.168.213.143:27118"));
        Assert.assertTrue(topicConfig3.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig4 = swallowClientConfig.getTopicConfig("topic4");
        Assert.assertTrue(topicConfig4.getGroup().equals("test"));
        Assert.assertTrue(topicConfig4.getSize() == 101);
        Assert.assertTrue(topicConfig4.getMax() == 102);
        Assert.assertTrue(topicConfig4.getStoreUrl().equals("mongodb://192.168.213.143:27118"));
        Assert.assertTrue(topicConfig4.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig5 = swallowClientConfig.getTopicConfig("topic5");
        Assert.assertTrue(topicConfig5.getGroup().equals("test"));
        Assert.assertTrue(topicConfig5.getSize() == null);
        Assert.assertTrue(topicConfig5.getMax() == null);
        Assert.assertTrue(topicConfig5.getStoreUrl() == null);
        Assert.assertTrue(topicConfig5.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig6 = swallowClientConfig.getTopicConfig("topic6");
        Assert.assertTrue(topicConfig6 == null);

        TopicConfig defaultConfig = swallowClientConfig.defaultTopicConfig();
        Assert.assertTrue(defaultConfig.getGroup().equals("default"));
        Assert.assertTrue(defaultConfig.getSize() == 100);
        Assert.assertTrue(defaultConfig.getMax() == 100);
        Assert.assertTrue(defaultConfig.getStoreUrl().equals("mongodb://192.168.213.143:27018"));
        Assert.assertTrue(defaultConfig.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

    }

    @Test
    public void testDevGroupConfig() {
        GroupConfig groupConfig1 = swallowClientConfig.getGroupConfig("group1");
        Assert.assertTrue(groupConfig1.getProducerIps().length == 0);
        Assert.assertTrue(groupConfig1.getConsumerIps().length == 1);

        GroupConfig groupConfig2 = swallowClientConfig.getGroupConfig("group2");
        Assert.assertTrue(groupConfig2.getProducerIps() == null);
        Assert.assertTrue(groupConfig2.getConsumerIps().length == 1);

        GroupConfig groupConfig3 = swallowClientConfig.getGroupConfig("group3");
        Assert.assertTrue(groupConfig3.getProducerIps() == null);
        Assert.assertTrue(groupConfig3.getConsumerIps() == null);

        GroupConfig groupConfig5 = swallowClientConfig.getGroupConfig("group5");
        Assert.assertTrue(groupConfig5.getProducerIps().length == 1);
        Assert.assertTrue(groupConfig5.getConsumerIps().length == 1);

        GroupConfig groupConfig6 = swallowClientConfig.getGroupConfig("group6");
        Assert.assertTrue(groupConfig6 == null);

    }

    @Test(expected = IllegalArgumentException.class)
      public void testDevBadGroupConfig() {
        GroupConfig groupConfig4 = swallowClientConfig.getGroupConfig("group4");
        Assert.assertTrue(groupConfig4.getProducerIps() == null);
        Assert.assertTrue(groupConfig4.getConsumerIps() == null);
    }

}
