package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.GroupConfig;
import com.dianping.swallow.common.internal.config.SwallowClientConfig;
import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.lion.LionUtilImpl;
import com.dianping.swallow.common.internal.util.EnvUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2016/01/26  上午11:10.
 */
public class SwallowClientConfigImplTest extends AbstractTest {

    private SwallowClientConfig swallowClientConfig;

    @Before
    public void beforeTest() throws Exception {
        swallowClientConfig = new SwallowClientConfigImpl();
    }

    private void addOrUpdateConfig(String name, String prefix, String config) {
        LionUtilImpl lionUtil = new LionUtilImpl();
        lionUtil.createOrSetConfig(prefix + "." + name, config);
    }

    @Override
    protected boolean useLocal() {
        return false;
    }

    @Test
    public void testTopicConfig() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"default\"}";
        String topicName = UUID.randomUUID().toString();

        System.out.println(topicName);

        TopicConfig rawTopicConfig = swallowClientConfig.getTopicConfig(topicName);

        Assert.assertTrue(rawTopicConfig == null);

        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        TopicConfig topicConfig = swallowClientConfig.getTopicConfig(topicName);

        Assert.assertTrue(topicConfig.getStoreUrl().equals("mongodb://192.168.213.143:27018"));
        Assert.assertTrue(topicConfig.getGroup().equals("default"));
        Assert.assertTrue(topicConfig.getMax() == 100);
        Assert.assertTrue(topicConfig.getSize() == 101);
        Assert.assertTrue(topicConfig.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        String strNewTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27017\",\"size\":100,\"max\":101,\"topicType\":\"EFFICIENCY_FIRST\",\"group\":\"test\"}";
        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strNewTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        TopicConfig newTopicConfig = swallowClientConfig.getTopicConfig(topicName);

        Assert.assertTrue(newTopicConfig.getStoreUrl().equals("mongodb://192.168.213.143:27017"));
        Assert.assertTrue(newTopicConfig.getGroup().equals("test"));
        Assert.assertTrue(newTopicConfig.getMax() == 101);
        Assert.assertTrue(newTopicConfig.getSize() == 100);
        Assert.assertTrue(newTopicConfig.getTopicType() == TOPIC_TYPE.EFFICIENCY_FIRST);

    }

    @Test
    public void testBadTopicConfig() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"default\"}";
        String topicName = UUID.randomUUID().toString();

        System.out.println(topicName);

        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        TopicConfig topicConfig = swallowClientConfig.getTopicConfig(topicName);

        Assert.assertTrue(topicConfig.getStoreUrl().equals("mongodb://192.168.213.143:27018"));
        Assert.assertTrue(topicConfig.getGroup().equals("default"));
        Assert.assertTrue(topicConfig.getMax() == 100);
        Assert.assertTrue(topicConfig.getSize() == 101);
        Assert.assertTrue(topicConfig.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);

        String strNewTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27017\",\"size\":100,\"max\"101,\"topicType\":\"EFFICIENCY_FIRST\",\"group\":\"test\"}";
        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strNewTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        TopicConfig newTopicConfig = swallowClientConfig.getTopicConfig(topicName);

        Assert.assertTrue(newTopicConfig.getStoreUrl().equals("mongodb://192.168.213.143:27018"));
        Assert.assertTrue(newTopicConfig.getGroup().equals("default"));
        Assert.assertTrue(newTopicConfig.getMax() == 100);
        Assert.assertTrue(newTopicConfig.getSize() == 101);
        Assert.assertTrue(newTopicConfig.getTopicType() == TOPIC_TYPE.DURABLE_FIRST);
    }

    @Test
    public void testGroupConfig() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String strGroupCfg = "{\"producerIps\":[\"192.168.213.143\"],\"consumerIps\":[\"192.168.213.143\"]}";
        String groupName = UUID.randomUUID().toString();

        System.out.println(groupName);
        GroupConfig rawGroupConfig = swallowClientConfig.getGroupConfig(groupName);
        Assert.assertTrue(rawGroupConfig == null);

        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        GroupConfig groupConfig = swallowClientConfig.getGroupConfig(groupName);
        Assert.assertTrue(groupConfig.getProducerIps().length == 1);
        Assert.assertTrue(groupConfig.getConsumerIps().length == 1);

        String strNewGroupCfg = "{\"producerIps\":[\"192.168.213.143\",\"192.168.212.143\"],\"consumerIps\":[\"192.168.213.141\",\"192.168.213.142\",\"192.168.213.143\"]}";

        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strNewGroupCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        GroupConfig newGroupConfig = swallowClientConfig.getGroupConfig(groupName);
        Assert.assertTrue(newGroupConfig.getProducerIps().length == 2);
        Assert.assertTrue(newGroupConfig.getConsumerIps().length == 3);

    }

    @Test
    public void testBadGroupConfig() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String strGroupCfg = "{\"producerIps\":[\"192.168.213.143\"],\"consumerIps\":[\"192.168.213.143\"]}";
        String groupName = UUID.randomUUID().toString();

        System.out.println(groupName);

        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        GroupConfig groupConfig = swallowClientConfig.getGroupConfig(groupName);
        Assert.assertTrue(groupConfig.getProducerIps().length == 1);
        Assert.assertTrue(groupConfig.getConsumerIps().length == 1);

        String strNewGroupCfg = "{\"producerIps\":\"192.168.213.143\",\"192.168.212.143\"],\"consumerIps\":[\"192.168.213.141\",\"192.168.213.142\",\"192.168.213.143\"]}";

        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strNewGroupCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        GroupConfig newGroupConfig = swallowClientConfig.getGroupConfig(groupName);
        Assert.assertTrue(newGroupConfig.getProducerIps().length == 1);
        Assert.assertTrue(newGroupConfig.getConsumerIps().length == 1);
    }
}
