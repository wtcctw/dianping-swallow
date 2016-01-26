package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.GroupConfig;
import com.dianping.swallow.common.internal.config.SwallowClientConfig;
import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.config.TopicConfig;
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
        TopicConfig topicConfig1 = swallowClientConfig.getTopicCfg("topic1");
        Assert.assertTrue(topicConfig1.getGroup() == null);
        Assert.assertTrue(topicConfig1.getSize() == 100);
        Assert.assertTrue(topicConfig1.getMax() == 100);
        Assert.assertTrue(topicConfig1.getStoreUrl() == null);
        Assert.assertTrue(topicConfig1.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig2 = swallowClientConfig.getTopicCfg("topic2");
        Assert.assertTrue(topicConfig2.getGroup() == null);
        Assert.assertTrue(topicConfig2.getSize() == null);
        Assert.assertTrue(topicConfig2.getMax() == null);
        Assert.assertTrue(topicConfig2.getStoreUrl() == null);
        Assert.assertTrue(topicConfig2.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig3 = swallowClientConfig.getTopicCfg("topic3");
        Assert.assertTrue(topicConfig3.getGroup() == null);
        Assert.assertTrue(topicConfig3.getSize() == 101);
        Assert.assertTrue(topicConfig3.getMax() == 102);
        Assert.assertTrue(topicConfig3.getStoreUrl().equals("mongodb://192.168.213.143:27118"));
        Assert.assertTrue(topicConfig3.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig4 = swallowClientConfig.getTopicCfg("topic4");
        Assert.assertTrue(topicConfig4.getGroup().equals("test"));
        Assert.assertTrue(topicConfig4.getSize() == 101);
        Assert.assertTrue(topicConfig4.getMax() == 102);
        Assert.assertTrue(topicConfig4.getStoreUrl().equals("mongodb://192.168.213.143:27118"));
        Assert.assertTrue(topicConfig4.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig5 = swallowClientConfig.getTopicCfg("topic5");
        Assert.assertTrue(topicConfig5.getGroup().equals("test"));
        Assert.assertTrue(topicConfig5.getSize() == null);
        Assert.assertTrue(topicConfig5.getMax() == null);
        Assert.assertTrue(topicConfig5.getStoreUrl() == null);
        Assert.assertTrue(topicConfig5.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig topicConfig6 = swallowClientConfig.getTopicCfg("topic6");
        Assert.assertTrue(topicConfig6.getGroup() == null);
        Assert.assertTrue(topicConfig6.getSize() == null);
        Assert.assertTrue(topicConfig6.getMax() == null);
        Assert.assertTrue(topicConfig6.getStoreUrl() == null);
        Assert.assertTrue(topicConfig6.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        TopicConfig defaultConfig = swallowClientConfig.defaultTopicCfg();
        Assert.assertTrue(defaultConfig.getGroup().equals("default"));
        Assert.assertTrue(defaultConfig.getSize() == 100);
        Assert.assertTrue(defaultConfig.getMax() == 100);
        Assert.assertTrue(defaultConfig.getStoreUrl().equals("mongodb://192.168.213.143:27018"));
        Assert.assertTrue(defaultConfig.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

    }

    @Test
    public void testDevGroupConfig() {
        GroupConfig groupConfig1 = swallowClientConfig.getGroupCfg("group1");
        Assert.assertTrue(groupConfig1.getProducerIps().length == 0);
        Assert.assertTrue(groupConfig1.getConsumerIps().length == 1);

        GroupConfig groupConfig2 = swallowClientConfig.getGroupCfg("group2");
        Assert.assertTrue(groupConfig2.getProducerIps() == null);
        Assert.assertTrue(groupConfig2.getConsumerIps().length == 1);

        GroupConfig groupConfig3 = swallowClientConfig.getGroupCfg("group3");
        Assert.assertTrue(groupConfig3.getProducerIps() == null);
        Assert.assertTrue(groupConfig3.getConsumerIps() == null);

        GroupConfig groupConfig4 = swallowClientConfig.getGroupCfg("group4");
        Assert.assertTrue(groupConfig4.getProducerIps() == null);
        Assert.assertTrue(groupConfig4.getConsumerIps() == null);

        GroupConfig groupConfig5 = swallowClientConfig.getGroupCfg("group5");
        Assert.assertTrue(groupConfig5.getProducerIps() == null);
        Assert.assertTrue(groupConfig5.getConsumerIps() == null);

        GroupConfig defaultConfig = swallowClientConfig.getGroupCfg("default");
        Assert.assertTrue(defaultConfig.getProducerIps().length == 1);
        Assert.assertTrue(defaultConfig.getConsumerIps().length == 1);
    }

}
