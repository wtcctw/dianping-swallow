package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午5:28:21
 */
public interface ConsumerServerResourceService extends ServerResourceService, ConfigChangeListener {

	boolean insert(ConsumerServerResource consumerServerResource);

	boolean update(ConsumerServerResource consumerServerResource);

	int remove(String ip);

	List<ConsumerServerResource> findAll();

	Pair<Long, List<ConsumerServerResource>> findConsumerServerResourcePage(int offset, int limit);

	ConsumerServerResource buildConsumerServerResource(String ip, String hostName, int port, int groupId,
			ServerType serverType);

	Pair<String, ResponseStatus> loadIdleConsumerServer();

	List<ConsumerServerResource> findByGroupId(long groupId);

	int getNextGroupId();

	String loadConsumerServerLionConfig();
	
	void setConsumerServerLionConfig(String consumerServerLionConfig);

}
