package com.dianping.swallow.web.controller.kafka.config;

import com.dianping.swallow.web.controller.kafka.OperationPathAware;
import org.apache.curator.framework.CuratorFramework;

import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:31.
 */
public interface TopicConfig extends OperationPathAware{

    void writeTopicConfig(CuratorFramework curator, String topic) throws Exception;

    void writeTopicConfig(CuratorFramework curator, String topic, Map<String, Object> config) throws Exception;

}
