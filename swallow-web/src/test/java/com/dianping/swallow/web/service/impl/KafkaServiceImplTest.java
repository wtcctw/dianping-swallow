package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.controller.kafka.config.TopicConfigBaseZkPath;
import com.dianping.swallow.web.controller.kafka.topic.AbstractZkPath;
import com.dianping.swallow.web.controller.kafka.topic.TopicBaseZkPath;
import org.junit.Before;
import org.junit.Test;

/**
 * Author   mingdongli
 * 16/3/1  下午11:49.
 */
public class KafkaServiceImplTest extends AbstractZkPath{

    private KafkaServiceImpl kafkaServiceImpl = new KafkaServiceImpl();

    private TopicBaseZkPath topicBaseZkPath;

    private TopicConfigBaseZkPath topicConfigBaseZkPath;

    @Before
    public void setUp() throws Exception {

        super.setUp();
        topicBaseZkPath = new TopicBaseZkPath();
        topicConfigBaseZkPath = new TopicConfigBaseZkPath();
        kafkaServiceImpl.setPartitionAssignment(topicBaseZkPath);
        kafkaServiceImpl.setTopicConfig(topicConfigBaseZkPath);
    }

    @Test
    public void testCreateTopic() throws Exception {

        kafkaServiceImpl.createTopic(zkServer, TOPIC, N_PARTITION, N_PARTITION);
        String topicPath = topicBaseZkPath.zkPath("topics/" + TOPIC);
        doDelZnodeRecursively(topicPath);
        String topicConfigPath = topicConfigBaseZkPath.zkPath(TOPIC);
        doDelZnodeRecursively(topicConfigPath);
    }
}