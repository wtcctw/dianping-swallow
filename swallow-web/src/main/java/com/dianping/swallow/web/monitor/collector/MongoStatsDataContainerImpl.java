package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.model.stats.MongoStatsData;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.MongoStatsDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/12/23  下午4:28.
 */
@Component
@Scope("prototype")
public class MongoStatsDataContainerImpl implements MongoStatsDataContainer{

    private long maxSize = 60;

    private NavigableMap<Long, MongoStatsData> mongoStatsDataMap = new ConcurrentSkipListMap<Long, MongoStatsData>();

    @Value("${swallow.web.monitor.keepinmemory}")
    public int keepInMemoryHour = 3;

    @Resource(name = "mongoStatsDataService")
    private MongoStatsDataService mongoStatsDataService;

    @PostConstruct
    public void initMembers(){
        maxSize = keepInMemoryHour * 60 * (60 / AbstractRetriever.DEFAULT_INTERVAL);
    }

    @Override
    public synchronized void add(Long time, MongoStatsData mongoStatsData) {

        MongoStatsData value = mongoStatsDataMap.get(time);

        if(value == null){
            while(isUpToMaxSize()){
                Long firstKey = mongoStatsDataMap.firstKey();
                mongoStatsDataMap.remove(firstKey);
            }
            mongoStatsDataMap.put(time, mongoStatsData);
        }else{
            value.merge(mongoStatsData);
        }

    }

    @Override
    public NavigableMap<Long, Long> retrieve(QPX qpx) {

        if(mongoStatsDataMap == null){
            return null;
        }

        NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
        for(Map.Entry<Long, MongoStatsData> entry : mongoStatsDataMap.entrySet()){
            result.put(entry.getKey(), entry.getValue().getQpx(qpx));
        }

        return result;
    }

    @Override
    public boolean isEmpty() {
        for(Map.Entry<Long, MongoStatsData> entry : mongoStatsDataMap.entrySet()){
            if(entry.getValue().getCount() > 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isUpToMaxSize() {
        return mongoStatsDataMap.size() >= maxSize;
    }

    @Override
    public void store(){
        Long time = mongoStatsDataMap.lastKey();
        mongoStatsDataService.insert(mongoStatsDataMap.get(time));
    }

    @Override
    public String toString() {
        return '{' +
                "mongoStatsDataMap=" + mongoStatsDataMap +
                '}';
    }
}
