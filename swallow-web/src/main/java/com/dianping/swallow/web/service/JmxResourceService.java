package com.dianping.swallow.web.service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.JmxResource;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/18  下午6:12.
 */
public interface JmxResourceService {

    boolean insert(JmxResource jmxResource);

    boolean update(JmxResource jmxResource);

    int remove(JmxResource jmxResource);

    List<JmxResource> findByGroup(String group);

    List<JmxResource> findAll();

    Pair<Long, List<JmxResource>> findJmxResourcePage(int offset, int limit);
}
