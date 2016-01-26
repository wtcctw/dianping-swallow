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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2016/01/26  下午3:41.
 */
public class SwallowPigeonLoadBalanceTest {

    private SwallowPigeonLoadBalance loadBalance;

    private String serviceName = "com.dianping.swallow.common.internal.producer.ProducerSwallowService";

    private List<Client> clients;

    private String[] producerIps = new String[]{"192.168.21.34", "192.168.21.35"};

    @Before
    public void beforeTest() {
        loadBalance = new SwallowPigeonLoadBalance();
        clients = new ArrayList<Client>();
        clients.add(new NettyClient(new ConnectInfo(serviceName, producerIps[0], 4000, 1)));
        clients.add(new NettyClient(new ConnectInfo(serviceName, producerIps[1], 4000, 1)));
    }

    private void addOrUpdateConfig(String name, String prefix, String config) {
        LionUtilImpl lionUtil = new LionUtilImpl();
        lionUtil.createOrSetConfig(prefix + name, config);
    }

    @Test
    public void testSelectClients0() {
        if (!EnvUtil.isAlpha()) {
            return;
        }

        String topicName = UUID.randomUUID().toString();

        List<Client> results = loadBalance.selectClients(clients, topicName);
        Assert.assertTrue(results.size() == 2);
        for (int i = 0; i < results.size(); i++) {
            Assert.assertTrue(results.get(i).getHost().equals(producerIps[i]));
        }
    }

    @Test
    public void testSelectClients1() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }
        String strGroupCfg = "{\"producerIps\":[\"192.168.21.34\"],\"consumerIps\":[\"192.168.21.35\"]}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);

        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        String topicName = UUID.randomUUID().toString();

        List<Client> results = loadBalance.selectClients(clients, topicName);
        Assert.assertTrue(results.size() == 2);
        for (int i = 0; i < results.size(); i++) {
            Assert.assertTrue(results.get(i).getHost().equals(producerIps[i]));
        }
    }

    @Test
    public void testSelectClients2() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }
        String strGroupCfg = "{\"producerIps\":[\"" + producerIps[0] + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);

        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"default\"}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        String topicName = UUID.randomUUID().toString();

        List<Client> results = loadBalance.selectClients(clients, topicName);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getHost().equals(producerIps[0]));
    }

    @Test
    public void testSelectClients3() throws InterruptedException {
        if (!EnvUtil.isAlpha()) {
            return;
        }
        String strDefaultGroupCfg = "{\"producerIps\":[\"" + producerIps[0] + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.GROUP_CFG_PREFIX, strDefaultGroupCfg);

        String strGroupCfg = "{\"producerIps\":[\"" + producerIps[1] + "\"],\"consumerIps\":[\"192.168.21.35\"]}";
        String groupName = UUID.randomUUID().toString();
        addOrUpdateConfig(groupName, SwallowClientConfigImpl.GROUP_CFG_PREFIX, strGroupCfg);


        String strDefaultTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"default\"}";
        addOrUpdateConfig("default", SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strDefaultTopicCfg);

        String topicName = UUID.randomUUID().toString();
        String strTopicCfg = "{\"storeUrl\":\"mongodb://192.168.213.143:27018\",\"size\":101,\"max\":100,\"group\":\"" + groupName + "\"}";
        addOrUpdateConfig(topicName, SwallowClientConfigImpl.TOPIC_CFG_PREFIX, strTopicCfg);
        TimeUnit.SECONDS.sleep(SwallowClientConfigImpl.CHECK_CONFIG_INTERVAL);

        List<Client> results = loadBalance.selectClients(clients, topicName);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getHost().equals(producerIps[1]));
    }
}
