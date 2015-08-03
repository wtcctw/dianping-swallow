package com.dianping.swallow.web.dao.impl.stats;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.dianping.swallow.web.dao.impl.AbstractDao;

public class AbstractStatsDao extends AbstractDao {

	@Resource(name = "alarmStatsMongoTemplate")
	protected MongoTemplate mongoTemplate;

}
