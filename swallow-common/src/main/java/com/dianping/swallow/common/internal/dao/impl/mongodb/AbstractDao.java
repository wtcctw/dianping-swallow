package com.dianping.swallow.common.internal.dao.impl.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.MongoManager;

/**
 * @author mengwenchao
 * 
 *         2015年7月14日 下午6:23:33
 */
public class AbstractDao {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected MongoManager mongoManager;

	public void setMongoManager(DefaultMongoManager mongoManager) {
		
		this.mongoManager = mongoManager;
	}

}
