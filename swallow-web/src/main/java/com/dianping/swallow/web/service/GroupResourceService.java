package com.dianping.swallow.web.service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.GroupResource;

import java.util.List;

/**
 * Author   mingdongli
 * 15/12/22  下午4:00.
 */
public interface GroupResourceService {

    boolean insert(GroupResource groupResource);

    boolean update(GroupResource groupResource);

    int remove(String groupName);

    Pair<Long, List<GroupResource>> findGroupResourcePage(int offset, int limit);

    GroupResource findDefault();

    List<GroupResource> findAll();
}
