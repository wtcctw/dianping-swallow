package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.KafkaServerResource;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/28  下午2:56.
 */
public interface KafkaServerResourceDao extends Dao{

    boolean insert(KafkaServerResource kafkaServerResource);

    boolean update(KafkaServerResource kafkaServerResource);

    int remove(String ip);

    long count();

    KafkaServerResource findByIp(String ip);

    List<KafkaServerResource> findByGroupName(String groupName);

    List<KafkaServerResource> findByGroupId(long groupId);

    List<KafkaServerResource> findAll();

    Pair<Long, List<KafkaServerResource>> findKafkaServerResourcePage(int offset, int limit);

    int getMaxGroupId();
}
