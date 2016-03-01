package com.dianping.swallow.web.controller.kafka.config;

import org.apache.curator.framework.CuratorFramework;

import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:31.
 */
public interface TopicConfig {

    void writeTopicConfig(CuratorFramework curator, String topic) throws Exception;

    void writeTopicConfig(CuratorFramework curator, String topic, Map<String, Object> config) throws Exception;

}
