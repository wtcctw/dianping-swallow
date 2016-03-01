package com.dianping.swallow.web.service;

import java.util.Map;

/**
 * Author   mingdongli
 * 16/3/1  下午4:45.
 */
public interface KafkaService {

    boolean createTopic(String zkServers, String topic, int partitions, int replicationFactor, Map<String, Object> topicConfig);
}