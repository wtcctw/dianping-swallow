package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.service.LogSearchService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qi.yin
 *         2015/11/17  下午8:04.
 */
@Service("logSearchService")
public class LogSearchServiceImpl implements LogSearchService {

    @Override
    public List<SearchResult> search(String topic, String cid, long mid) {
        return null;
    }
}
