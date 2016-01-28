package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.KafkaServerResourceDao;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/28  下午3:23.
 */
@Service("kafkaServerResourceService")
public class KafkaServerResourceServiceImpl extends AbstractSwallowService implements KafkaServerResourceService{

    @Autowired
    private KafkaServerResourceDao kafkaServerResourceDao;

    @Override
    public boolean insert(KafkaServerResource kafkaServerResource) {
        return kafkaServerResourceDao.insert(kafkaServerResource);
    }

    @Override
    public boolean update(KafkaServerResource kafkaServerResource) {
        return kafkaServerResourceDao.update(kafkaServerResource);
    }

    @Override
    public int remove(String ip) {
        return kafkaServerResourceDao.remove(ip);
    }

    @Override
    public KafkaServerResource findByIp(String ip) {
        return kafkaServerResourceDao.findByIp(ip);
    }

    @Override
    public List<KafkaServerResource> findByGroupName(String groupName) {
        return kafkaServerResourceDao.findByGroupName(groupName);
    }

    @Override
    public List<KafkaServerResource> findByGroupId(long groupId) {
        return kafkaServerResourceDao.findByGroupId(groupId);
    }

    @Override
    public List<KafkaServerResource> findAll() {
        return kafkaServerResourceDao.findAll();
    }

    @Override
    public Pair<Long, List<KafkaServerResource>> findKafkaServerResourcePage(int offset, int limit) {
        return kafkaServerResourceDao.findKafkaServerResourcePage(offset, limit);
    }

    @Override
    public int getNextGroupId() {
        return kafkaServerResourceDao.getMaxGroupId() + 1;
    }
}
