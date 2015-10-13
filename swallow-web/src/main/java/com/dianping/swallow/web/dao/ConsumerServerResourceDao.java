package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午4:56:13
 */
public interface ConsumerServerResourceDao extends ServerResourceDao {

	boolean insert(ConsumerServerResource consumerServerResource);

	boolean update(ConsumerServerResource consumerServerResource);

	int remove(String ip);

	long count();

	ConsumerServerResource findByIp(String ip);

	List<ConsumerServerResource> findByGroupId(long groupId);

	ConsumerServerResource findDefault();

	List<ConsumerServerResource> findAll();

	Pair<Long, List<ConsumerServerResource>> findConsumerServerResourcePage(int offset, int limit);

	ConsumerServerResource loadIdleConsumerServer();

	int getMaxGroupId();
}
