package com.dianping.swallow.web.service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月16日下午6:57:01
 */
public interface TopicApplyService{

	Pair<String, ResponseStatus> chooseSearchMongoDb();
	
	Pair<String, ResponseStatus> chooseMongoDbWithoutSearch();
	
	Pair<String, ResponseStatus> chooseConsumerServer();
	
	String getBestMongo();

	String getSearchMongo();

	String getBestConsumerServer();
}
