package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.JmxResource;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/18  下午5:58.
 */
public interface JmxResourceDao extends Dao{

    boolean insert(JmxResource jmxResource);

    boolean update(JmxResource jmxResource);

    int remove(JmxResource jmxResource);

    long count();

    List<JmxResource> findByName(String name);

    List<JmxResource> findAll();

    Pair<Long, List<JmxResource>> findJmxResourcePage(int offset, int limit);

}
