package com.dianping.swallow.web.service.impl;

import com.dianping.elasticsearch.conditions.Conditions;
import com.dianping.elasticsearch.query.ESSearch;
import com.dianping.elasticsearch.services.ElasticSearchService;
import com.dianping.swallow.web.service.LogSearchService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qi.yin
 *         2015/11/17  下午8:04.
 */
@Service("logSearchService")
public class LogSearchServiceImpl implements LogSearchService {

    private static final Logger logger = LogManager.getLogger(LogSearchServiceImpl.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Override
    public List<SearchResult> search(String topic, String cid, long mid) {
        List<SearchResult> results = null;
        try {
            ESSearch search = elasticSearchService.buildSearch("dpods_log_swallow-consumer")
                    .addCondition(Conditions.term("topic", topic))
                    .addCondition(Conditions.term("cid", cid))
                    .addCondition(Conditions.or(Conditions.term("mid", mid),
                            Conditions.term("bmid", mid)));

            List<Map<String, Object>> tempResults = elasticSearchService.search(search);
            if (tempResults != null && !tempResults.isEmpty()) {

                results = new ArrayList<SearchResult>();
                for (Map<String, Object> tempMap : tempResults) {

                    if (tempMap != null && !tempMap.isEmpty()) {
                        SearchResult result = new SearchResult();
                        result.setTopic(String.valueOf(tempMap.get("topic")));
                        result.setCid(String.valueOf(tempMap.get("cid")));
                        result.setDate(String.valueOf(tempMap.get("date")));
                        result.setType(String.valueOf(tempMap.get("type")));
                        result.setIp(String.valueOf(tempMap.get("ip")));
                        long tempMid = Long.valueOf(String.valueOf(tempMap.get("mid")));
                        result.setMid(tempMid);
                        results.add(result);
                        if (tempMid != mid) {
                            if (tempMap.containsKey("bmid")) {
                                long bmid = Long.valueOf(String.valueOf(tempMap.get("bmid")));
                                if (bmid == mid) {
                                    result.setMid(bmid);
                                }
                            }
                        }
                    }
                }

            }
        }catch (Exception e){
            logger.error("[search] failed.",e);
        }
        return results;
    }
}