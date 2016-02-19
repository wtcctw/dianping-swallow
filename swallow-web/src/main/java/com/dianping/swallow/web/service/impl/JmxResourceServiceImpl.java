package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.JmxResourceDao;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.JmxResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/18  下午6:12.
 */
@Service("jmxResourceService")
public class JmxResourceServiceImpl extends AbstractSwallowService implements JmxResourceService {

    @Autowired
    private JmxResourceDao jmxResourceDao;

    @Override
    public boolean insert(JmxResource jmxResource) {
        return jmxResourceDao.insert(jmxResource);
    }

    @Override
    public boolean update(JmxResource jmxResource) {
        return jmxResourceDao.update(jmxResource);
    }

    @Override
    public int remove(JmxResource jmxResource) {
        return jmxResourceDao.remove(jmxResource);
    }

    @Override
    public List<JmxResource> findByName(String name) {
        return jmxResourceDao.findByName(name);
    }

    @Override
    public List<JmxResource> findAll() {
        return jmxResourceDao.findAll();
    }

    @Override
    public Pair<Long, List<JmxResource>> findJmxResourcePage(int offset, int limit) {
        return jmxResourceDao.findJmxResourcePage(offset, limit);
    }
}
