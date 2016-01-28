package com.dianping.swallow.web.service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.KafkaServerResource;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/28  下午3:19.
 */
public interface KafkaServerResourceService {

    boolean insert(KafkaServerResource kafkaServerResource);

    boolean update(KafkaServerResource kafkaServerResource);

    int remove(String ip);

    KafkaServerResource findByIp(String ip);

    List<KafkaServerResource> findByGroupName(String groupName);

    List<KafkaServerResource> findByGroupId(long groupId);

    List<KafkaServerResource> findAll();

    Pair<Long, List<KafkaServerResource>> findKafkaServerResourcePage(int offset, int limit);

    int getNextGroupId();
}
