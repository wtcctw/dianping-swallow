package com.dianping.swallow.web.dao.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.dianping.swallow.web.service.AccessControlServiceImpl;
import com.dianping.swallow.web.service.AdministratorService;


/**
 * @author mingdongli
 *
 * 2015年5月8日 下午4:39:18
 */
public abstract class AbstractWriteDao extends AbstractDao{
	
	@Resource( name = "topicMongoTemplate")
	protected MongoTemplate 						mongoTemplate;
	
	@Resource(name = "accessControlService")
	protected AccessControlServiceImpl 				accessControlService;
	
    @Resource(name = "administratorService")
    protected AdministratorService 					administratorService;
	
}

