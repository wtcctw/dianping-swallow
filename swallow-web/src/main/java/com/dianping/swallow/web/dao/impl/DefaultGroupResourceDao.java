package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.GroupResourceDao;
import com.dianping.swallow.web.model.resource.GroupResource;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 15/12/22  下午3:53.
 */
@Component
public class DefaultGroupResourceDao extends AbstractWriteDao implements GroupResourceDao {

    private static final String GROUPRESOURCE_COLLECTION = "GROUP_RESOURCE";

    private static final String GROUPNAME = "groupName";

    private static final String DEFAULT = "GENERAL";

    @Override
    public boolean insert(GroupResource groupResource) {

        try {
            mongoTemplate.save(groupResource, GROUPRESOURCE_COLLECTION);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[insert] error when save producer server stats data " + groupResource, e);
            }
        }
        return false;
    }

    @Override
    public boolean update(GroupResource groupResource) {

        return insert(groupResource);
    }

    @Override
    public int remove(String groupName) {
        Query query = new Query(Criteria.where(GROUPNAME).is(groupName));
        WriteResult result = mongoTemplate.remove(query, GroupResource.class, GROUPRESOURCE_COLLECTION);
        return result.getN();
    }

    @Override
    public long count() {

        Query query = new Query();
        return mongoTemplate.count(query, GROUPRESOURCE_COLLECTION);
    }

    @Override
    public Pair<Long, List<GroupResource>> findGroupResourcePage(int offset, int limit) {

        Query query = new Query();

        query.skip(offset).limit(limit);
        List<GroupResource> groupResource = mongoTemplate.find(query, GroupResource.class, GROUPRESOURCE_COLLECTION);
        Long size = this.count();
        return new Pair<Long, List<GroupResource>>(size, groupResource);
    }

    @Override
    public List<GroupResource> findAll(){
        List<GroupResource> groupResource = mongoTemplate.findAll(GroupResource.class, GROUPRESOURCE_COLLECTION);
        return groupResource;
    }

    @Override
    public GroupResource findDefault() {

        Query query = new Query(Criteria.where(GROUPNAME).is(DEFAULT));
        GroupResource groupResource = mongoTemplate.findOne(query, GroupResource.class, GROUPRESOURCE_COLLECTION);
        return groupResource;
    }
}
