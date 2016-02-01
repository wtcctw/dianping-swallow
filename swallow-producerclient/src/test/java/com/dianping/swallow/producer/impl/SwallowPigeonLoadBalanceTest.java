package com.dianping.swallow.producer.impl;

import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.domain.ConnectInfo;
import com.dianping.pigeon.remoting.netty.invoker.NettyClient;
import com.dianping.swallow.common.internal.config.impl.SwallowClientConfigImpl;
import com.dianping.swallow.common.internal.config.impl.lion.LionUtilImpl;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.producer.impl.internal.SwallowPigeonLoadBalance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2016/01/26  下午3:41.
 */
public class SwallowPigeonLoadBalanceTest {

    private SwallowPigeonLoadBalance loadBalance;

    private String serviceName = "com.dianping.swallow.common.internal.producer.ProducerSwallowService";

    private List<Client> clients;

    private List<String> producerIps = new ArrayList<String>();

    @Before
    public void beforeTest() {
        loadBalance = new SwallowPigeonLoadBalance();
        producerIps.add("192.168.21.34");
        producerIps.add("192.168.21.35");
        producerIps.add("192.168.21.36");
        clients = new ArrayList<Client>();
        clients.add(new NettyClient(new ConnectInfo(serviceName, producerIps.get(0), 4000, 1)));
        clients.add(new NettyClient(new ConnectInfo(serviceName, producerIps.get(1), 4000, 1)));
        clients.add(new NettyClient(new ConnectInfo(serviceName, producerIps.get(2), 4000, 1)));
    }

    private void addOrUpdateConfig(String name, String prefix, String config) {
        LionUtilImpl lionUtil = new LionUtilImpl();
        lionUtil.createOrSetConfig(prefix + "." + name, config);
    }

    @Test
    public void testSelectClients0() {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String topicName = UUID.randomUUID().toString();

        Client result = loadBalance.selectClient(clients, topicName);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testSelectClients1() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }
        List<String> topicIps = new ArrayList<String>();
        topicIps.add("192.168.21.34");

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);

        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        String topicName = UUID.randomUUID().toString();

        Client result = loadBalance.selectClient(clients, topicName);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testSelectClients2() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }
        List<String> topicIps = new ArrayList<String>();
        topicIps.add("192.168.21.34");

        String strGroupCfg = "{\"producerIps\":[\"" + topicIps.get(0) + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        addOrUpdateConfig("test", SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"test\"}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        String topicName = UUID.randomUUID().toString();

        for (int i = 0; i < 10; i++) {
            Client result = loadBalance.selectClient(clients, topicName);
            Assert.assertTrue(topicIps.contains(result.getHost()));
        }
    }

    @Test
    public void testSelectClients3() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        List<String> topicIps = new ArrayList<String>();
        topicIps.add("192.168.21.35");

        String strGroupCfg = "{\"producerIps\":[\"" + topicIps.get(0) + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        String groupName = UUID.randomUUID().toString();
        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);

        String topicName = UUID.randomUUID().toString();
        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"" + groupName + "\"}";
        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        for (int i = 0; i < 10; i++) {
            Client result = loadBalance.selectClient(clients, topicName);
            Assert.assertTrue(topicIps.contains(result.getHost()));
        }
    }

    @Test
    public void testSelectClients4() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        List<String> topicIps = new ArrayList<String>();
        topicIps.add("192.168.21.38");

        String strGroupCfg = "{\"producerIps\":[\"" + topicIps.get(0) + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        String groupName = UUID.randomUUID().toString();
        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);

        String topicName = UUID.randomUUID().toString();
        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"" + groupName + "\"}";
        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        for (int i = 0; i < 10; i++) {
            Client result = loadBalance.selectClient(clients, topicName);
            Assert.assertTrue(producerIps.contains(result.getHost()));
        }
    }
}
