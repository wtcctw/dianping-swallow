package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.GroupResource;

import java.util.List;

/**
 * Author   mingdongli
 * 15/12/22  下午3:49.
 */
public interface GroupResourceDao extends Dao{

    boolean insert(GroupResource groupResource);

    boolean update(GroupResource groupResource);

    int remove(String groupName);

    long count();

    Pair<Long, List<GroupResource>> findGroupResourcePage(int offset, int limit);

    List<GroupResource> findAll();

    GroupResource findDefault();

}
