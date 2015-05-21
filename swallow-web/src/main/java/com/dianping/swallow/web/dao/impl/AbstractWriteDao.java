package com.dianping.swallow.web.dao.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * @author mingdongli
 *
 * 2015年5月8日 下午4:39:18
 */
public abstract class AbstractWriteDao extends AbstractDao{
	
	@Resource( name = "topicMongoTemplate")
	protected MongoTemplate 						mongoTemplate;

}

