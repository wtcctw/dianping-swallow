package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.JmxResourceDao;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 16/2/18  下午6:01.
 */
@Component
public class DefaultJmxResourceDao extends AbstractWriteDao implements JmxResourceDao {

    private static final String JMXRESOURCE_COLLECTION = "JMX_RESOURCE";

    private static final String GROUP = "group";

    private static final String NAME = "name";

    private static final String TYPE = "type";

    @Override
    public boolean insert(JmxResource jmxResource) {
        try {
            mongoTemplate.save(jmxResource, JMXRESOURCE_COLLECTION);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[insert] error when save jmx resource data " + jmxResource, e);
            }
        }
        return false;
    }

    @Override
    public boolean update(JmxResource jmxResource) {
        return insert(jmxResource);
    }

    @Override
    public int remove(JmxResource jmxResource) {
        Query query = new Query(Criteria.where(GROUP).is(jmxResource.getGroup()).and(NAME).is(jmxResource.getName()).and(TYPE).is(jmxResource.getType()));
        WriteResult result = mongoTemplate.remove(query, JmxResource.class, JMXRESOURCE_COLLECTION);
        return result.getN();
    }

    @Override
    public long count() {
        Query query = new Query();
        return mongoTemplate.count(query, JMXRESOURCE_COLLECTION);
    }

    @Override
    public List<JmxResource> findByName(String name) {
        Query query = new Query(Criteria.where(NAME).is(name));
        List<JmxResource> jmxResources = mongoTemplate.find(query, JmxResource.class, JMXRESOURCE_COLLECTION);
        return jmxResources;
    }

    @Override
    public List<JmxResource> findAll() {
        List<JmxResource> jmxResources = mongoTemplate.findAll(JmxResource.class, JMXRESOURCE_COLLECTION);
        return jmxResources;
    }

    @Override
    public Pair<Long, List<JmxResource>> findJmxResourcePage(int offset, int limit) {
        Query query = new Query();

        query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Sort.Direction.ASC, GROUP)));
        List<JmxResource> jmxResources = mongoTemplate.findAll(JmxResource.class, JMXRESOURCE_COLLECTION);
        Long size = this.count();
        return new Pair<Long, List<JmxResource>>(size, jmxResources);
    }
}
