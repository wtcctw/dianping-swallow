package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.dao.MongoStatsDataDao;
import com.dianping.swallow.web.model.stats.MongoStatsData;
import com.dianping.swallow.web.service.MongoStatsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Author   mingdongli
 * 15/12/23  下午6:14.
 */
@Service("mongoStatsDataService")
public class MongoStatsDataServiceImpl implements MongoStatsDataService{

    @Autowired
    private MongoStatsDataDao mongoStatsDataDao;

    @Override
    public boolean insert(MongoStatsData mongoStatsData) {

        return mongoStatsDataDao.insert(mongoStatsData);
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> findSectionQpsData(QPX qpx, long startKey, long endKey) {

        List<MongoStatsData> mognoStatsDatas = mongoStatsDataDao.findSectionData(startKey, endKey);
        Map<String, NavigableMap<Long, Long>> mongoStatsDataMaps = null;

        if (mognoStatsDatas != null) {

            mongoStatsDataMaps = new HashMap<String, NavigableMap<Long, Long>>();
            for (MongoStatsData mongoStatsData : mognoStatsDatas) {

                if (mongoStatsDataMaps.containsKey(mongoStatsData.getIps())) {

                    NavigableMap<Long, Long> mongoStatsDataMap = mongoStatsDataMaps.get(mongoStatsData.getIps());
                    mongoStatsDataMap.put(mongoStatsData.getTimeKey(), mongoStatsData.getQpx(qpx));
                    mongoStatsDataMaps.put(mongoStatsData.getIps(), mongoStatsDataMap);

                } else {

                    NavigableMap<Long, Long> mongoStatsDataMap = new TreeMap<Long, Long>();
                    mongoStatsDataMap.put(mongoStatsData.getTimeKey(), mongoStatsData.getQpx(qpx));
                    mongoStatsDataMaps.put(mongoStatsData.getIps(), mongoStatsDataMap);
                }
            }
        }
        return mongoStatsDataMaps;
    }
}
