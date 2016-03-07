package com.dianping.swallow.kafka.admin;

import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Before;
import org.junit.Test;

/**
 * Author   mingdongli
 * 16/3/7  上午11:12.
 */
public class AdminUtilTest {

    private static final String serverList = "192.168.226.41:2181";

    private ZkClient zkClient;

    @Before
    public void setUp() throws Exception {
        zkClient = new ZkClient(serverList,5000, 5000, ZKStringSerializer$.MODULE$);
    }

    @Test
    public void testCreateTopic(){
        AdminUtil.createTopic(zkClient, "test", 1, 2);
        AdminUtil.topicExists(zkClient, "test");
    }

}