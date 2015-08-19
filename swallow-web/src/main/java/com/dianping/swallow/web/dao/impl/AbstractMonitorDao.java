package com.dianping.swallow.web.dao.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * @author mengwenchao
 *
 * 2015年4月17日 下午4:09:14
 */
public abstract class AbstractMonitorDao extends AbstractDao{
	
	@Resource( name = "statsMongoTemplate")
	protected MongoTemplate mongoTemplate; 
	

}
