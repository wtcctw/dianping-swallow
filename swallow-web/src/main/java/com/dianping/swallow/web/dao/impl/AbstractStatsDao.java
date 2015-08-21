package com.dianping.swallow.web.dao.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.dianping.swallow.web.dao.impl.AbstractDao;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:38:35
 */
public class AbstractStatsDao extends AbstractDao {

	@Resource(name = "statsDataMongoTemplate")
	protected MongoTemplate mongoTemplate;

}
