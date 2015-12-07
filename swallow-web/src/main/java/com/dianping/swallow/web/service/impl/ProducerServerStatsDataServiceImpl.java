package com.dianping.swallow.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 下午3:17:48
 */
@Service("producerServerStatsDataService")
public class ProducerServerStatsDataServiceImpl implements ProducerServerStatsDataService {

    @Autowired
    private ProducerServerStatsDataDao producerServerStatsDataDao;

    @Override
    public boolean insert(ProducerServerStatsData serverStatsData) {
        return producerServerStatsDataDao.insert(serverStatsData);
    }

    @Override
    public boolean insert(List<ProducerServerStatsData> serverStatsDatas) {
        return producerServerStatsDataDao.insert(serverStatsDatas);
    }

    @Override
    public boolean removeLessThanTimeKey(long timeKey) {
        return producerServerStatsDataDao.removeLessThanTimeKey(timeKey);
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> findSectionQpsData(long startKey, long endKey) {
        List<ProducerServerStatsData> serverStatsDatas = producerServerStatsDataDao.findSectionData(startKey, endKey);
        Map<String, NavigableMap<Long, Long>> serverStatsDataMaps = null;

        if (serverStatsDatas != null) {

            serverStatsDataMaps = new HashMap<String, NavigableMap<Long, Long>>();
            for (ProducerServerStatsData serverStatsData : serverStatsDatas) {

                if (serverStatsDataMaps.containsKey(serverStatsData.getIp())) {

                    NavigableMap<Long, Long> serverStatsDataMap = serverStatsDataMaps.get(serverStatsData.getIp());
                    serverStatsDataMap.put(serverStatsData.getTimeKey(), serverStatsData.getQps());
                    serverStatsDataMaps.put(serverStatsData.getIp(), serverStatsDataMap);

                } else {

                    NavigableMap<Long, Long> serverStatsDataMap = new TreeMap<Long, Long>();
                    serverStatsDataMap.put(serverStatsData.getTimeKey(), serverStatsData.getQps());
                    serverStatsDataMaps.put(serverStatsData.getIp(), serverStatsDataMap);
                }
            }
        }
        return serverStatsDataMaps;
    }
}
