package com.dianping.swallow.web.controller.kafka.config;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.controller.kafka.AbstractDummyBaseZkPath;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:18.
 */
@Component
public class TopicConfigBaseZkPath extends AbstractDummyBaseZkPath implements TopicConfig {

    private static final String TOPIC_CONFIG = "/config/topics";

    @Override
    protected String baseZkPath() {
        return TOPIC_CONFIG;
    }

    @Override
    public void writeTopicConfig(CuratorFramework curator, String topic) throws Exception {
        writeTopicConfig(curator, topic, new HashMap<String, Object>());
    }

    @Override
    public void writeTopicConfig(CuratorFramework curator, String topic, Map<String, Object> config) throws Exception {

        String topicConfigPath = zkPath(topic);
        Map<String, Object> mapConfig = new HashMap<String, Object>();
        mapConfig.put("version", 1);
        mapConfig.put("config", config);
        JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
        String topicConfigJson = jsonBinder.toJson(mapConfig);
        updatePersistentPath(curator, topicConfigPath, topicConfigJson);

    }

    @Override
    public String operationPath() {
        return TOPIC_CONFIG;
    }
}
