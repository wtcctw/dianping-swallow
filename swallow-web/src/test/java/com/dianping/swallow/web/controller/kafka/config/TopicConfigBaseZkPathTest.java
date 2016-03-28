package com.dianping.swallow.web.controller.kafka.config;

import com.dianping.swallow.web.controller.kafka.topic.AbstractZkPath;
import org.junit.Test;

/**
 * Author   mingdongli
 * 16/3/1  下午11:37.
 */
public class TopicConfigBaseZkPathTest extends AbstractZkPath {

    private TopicConfigBaseZkPath topicConfigBaseZkPath = new TopicConfigBaseZkPath();

    @Test
    public void testWriteTopicConfig() throws Exception {

        topicConfigBaseZkPath.writeTopicConfig(curator, TOPIC);
        String topicConfigPath = topicConfigBaseZkPath.zkPath(TOPIC);
        doDelZnodeRecursively(topicConfigPath);
    }
}