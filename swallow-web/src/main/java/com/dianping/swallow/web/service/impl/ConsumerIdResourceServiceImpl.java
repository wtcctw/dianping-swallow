package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingdongli
 *         <p/>
 *         2015年8月11日上午10:34:28
 */
@Service("consumerIdResourceService")
public class ConsumerIdResourceServiceImpl extends AbstractSwallowService implements ConsumerIdResourceService {

    @Autowired
    private ConsumerIdResourceDao consumerIdResourceDao;

    @Override
    public boolean insert(ConsumerIdResource consumerIdResource) {

        return consumerIdResourceDao.insert(consumerIdResource);
    }

    @Override
    public boolean update(ConsumerIdResource consumerIdResource) {

        return consumerIdResourceDao.update(consumerIdResource);
    }

    @Override
    public int remove(String topic, String consumerid) {

        return consumerIdResourceDao.remove(topic, consumerid);
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdParam consumerIdParam) {

        return consumerIdResourceDao.findByTopic(consumerIdParam);
    }

    @Override
    public List<ConsumerIdResource> findByTopic(String topic) {
        return consumerIdResourceDao.findByTopic(topic);
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> find(ConsumerIdParam consumerIdParam) {

        return consumerIdResourceDao.find(consumerIdParam);
    }

    @Override
    public List<ConsumerIdResource> findAll(String... fields) {

        return consumerIdResourceDao.findAll(fields);
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdParam consumerIdParam) {

        return consumerIdResourceDao.findConsumerIdResourcePage(consumerIdParam);
    }

    @Override
    public ConsumerIdResource buildConsumerIdResource(String topic, String consumerId) {

        ConsumerIdResource consumerIdResource = new ConsumerIdResource();
        consumerIdResource.setAlarm(Boolean.TRUE);
        consumerIdResource.setTopic(topic);
        consumerIdResource.setConsumerId(consumerId);

        consumerIdResource.setConsumerIpInfos(new ArrayList<IpInfo>());
        consumerIdResource.setConsumerApplications(new ArrayList<String>());

        ConsumerIdResource defaultResource = consumerIdResourceDao.findDefault();
        if (defaultResource == null) {
            throw new RuntimeException("No default configuration for ConsumerIdResource");
        }
        consumerIdResource.setConsumerAlarmSetting(defaultResource.getConsumerAlarmSetting());

        return consumerIdResource;
    }

    @Override
    public ConsumerIdResource findByConsumerIdAndTopic(String topic, String consumerId) {

        return consumerIdResourceDao.findByConsumerIdAndTopic(topic, consumerId);
    }

    @Override
    public long countInactive() {

        return consumerIdResourceDao.countInactive();
    }

}
