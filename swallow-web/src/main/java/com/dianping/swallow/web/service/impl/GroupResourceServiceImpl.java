package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.GroupResourceDao;
import com.dianping.swallow.web.model.resource.GroupResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.GroupResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   mingdongli
 * 15/12/22  下午4:01.
 */
@Service("groupResourceService")
public class GroupResourceServiceImpl extends AbstractSwallowService implements GroupResourceService{

    @Autowired
    private GroupResourceDao groupResourceDao;

    @Override
    public boolean insert(GroupResource groupResource) {
        return groupResourceDao.insert(groupResource);
    }

    @Override
    public boolean update(GroupResource groupResource) {
        return groupResourceDao.update(groupResource);
    }

    @Override
    public int remove(String groupName) {
        return groupResourceDao.remove(groupName);
    }

    @Override
    public Pair<Long, List<GroupResource>> findGroupResourcePage(int offset, int limit) {
        return groupResourceDao.findGroupResourcePage(offset, limit);
    }

    @Override
    public List<String> findAllGroupName(){
        List<GroupResource> groupResources = groupResourceDao.findAll();
        List<String> list = new ArrayList<String>();

        for (GroupResource groupResource : groupResources) {
            String groupName = groupResource.getGroupName();
            if (!list.contains(groupName)) {
                list.add(groupName);
            }
        }

        return list;
    }

    @Override
    public GroupResource findDefault() {
        return groupResourceDao.findDefault();
    }
}
