package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.dao.MongoStatsDataDao;
import com.dianping.swallow.web.model.stats.MongoStatsData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 15/12/23  下午6:02.
 */
@Component
public class DefaultMongoStatsDataDao extends AbstractStatsDao implements MongoStatsDataDao {

    private static final String MONGOSTATSDATA_COLLECTION = "MONGO_STATS_DATA";

    private static final String TIMEKEY = "timeKey";

    @Override
    public boolean insert(MongoStatsData mongoStatsData) {
        try {
            mongoTemplate.save(mongoStatsData, MONGOSTATSDATA_COLLECTION);
            return true;
        } catch (Exception e) {
            logger.error("Error when save mongo statsdata." + mongoStatsData, e);
        }
        return false;
    }


    @Override
    public boolean removeLessThanTimeKey(long timeKey) {
        try {
            Query query = new Query(Criteria.where(TIMEKEY).lt(timeKey));
            mongoTemplate.remove(query, MONGOSTATSDATA_COLLECTION);
            return true;
        } catch (Exception e) {
            logger.error("[removeLessThanTimeKey] remove less than timeKey error.", e);
        }
        return false;
    }

    @Override
    public List<MongoStatsData> findSectionData(long startKey, long endKey) {

        Query query = new Query(Criteria.where(TIMEKEY).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(
                Sort.Direction.ASC, TIMEKEY)));
        List<MongoStatsData> mongoStatsDatas = mongoTemplate.find(query, MongoStatsData.class, MONGOSTATSDATA_COLLECTION);
        return mongoStatsDatas;
    }

    @Override
    public MongoStatsData findOldestData() {
        Query query = new Query();
        query.skip(0).limit(1).with(new Sort(new Sort.Order(Sort.Direction.ASC, TIMEKEY)));
        MongoStatsData statsData = mongoTemplate.findOne(query, MongoStatsData.class,
                MONGOSTATSDATA_COLLECTION);
        return statsData;
    }
}
