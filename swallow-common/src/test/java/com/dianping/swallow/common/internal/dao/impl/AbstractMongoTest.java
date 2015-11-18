package com.dianping.swallow.common.internal.dao.impl;

import java.util.List;


import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.ServerAddress;


/**
 * @author mengwenchao
 *
 * 2015年10月13日 上午11:50:32
 */
public class AbstractMongoTest extends AbstractDbTest{
	
	
	protected List<ServerAddress> getServerSeeds(String mongoAddress){
		
		return MongoUtils.parseUriToAddressList(mongoAddress);
	}
	
}
